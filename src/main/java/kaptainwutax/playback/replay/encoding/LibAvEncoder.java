package kaptainwutax.playback.replay.encoding;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVIOContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.avutil.AVRational;
import org.bytedeco.ffmpeg.swscale.SwsContext;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swscale.*;

public class LibAvEncoder extends Encoder<LibAvEncoder.Options> {
    private final AVFormatContext formatCtx = avformat_alloc_context();
    private final AVCodec videoCodec;
    private final AVStream videoStream;
    private final AVCodecContext videoCtx;
    private final ByteBuffer frame;
    private final int stride;
    private final AVFrame codecFrame = av_frame_alloc();
    private final SwsContext swsContext;
    private long frames = 0;
    private String filename = "test.mp4";

    public LibAvEncoder(Options options) throws IOException {
        super(options);
        avformat_alloc_output_context2(this.formatCtx, null, "mp4", null);
        this.videoCodec = avcodec_find_encoder(AV_CODEC_ID_H264);
        this.videoCtx = avcodec_alloc_context3(this.videoCodec);
        this.videoStream = avformat_new_stream(this.formatCtx, this.videoCodec);
        this.videoStream.codec(this.videoCtx);
        AVRational frameRate = rational(options.frameRate);
        this.videoCtx.framerate(frameRate);
        this.videoStream.avg_frame_rate(frameRate);
        this.videoStream.time_base(rational(frameRate.den(), frameRate.num()));
        this.videoCtx.time_base(rational(frameRate.den(), frameRate.num()));
        this.videoCtx.bit_rate(4000_000);
        this.videoCtx.gop_size(10);
        this.videoCtx.max_b_frames(1);
        int codecPixFmt = AV_PIX_FMT_YUV420P;
        int width = options.width;
        int height = options.height;
        this.videoCtx.width(width).height(height).pix_fmt(codecPixFmt);
        this.codecFrame.width(width).height(height).format(codecPixFmt);
        this.frame = aligned(width * height * 3, 32);
        this.stride = width * 3;
        this.swsContext = sws_getContext(width, height, AV_PIX_FMT_RGB24, width, height, codecPixFmt, SWS_BICUBIC, null, null, (double[]) null);
        if (av_frame_get_buffer(this.codecFrame, 32) != 0) {
            throw new IOException("Could not allocate buffer for frame");
        }
    }

    @Override
    public void open() throws IOException {
        AVIOContext ioCtx = new AVIOContext();
        int ret = avio_open(ioCtx, filename, AVIO_FLAG_WRITE);
        if (ret < 0) throw new IOException("Could not open output file for writing");
        this.formatCtx.pb(ioCtx);
        if (avformat_write_header(this.formatCtx, (AVDictionary) null) < 0) {
            throw new IOException("Could not write header");
        }
        if (avcodec_open2(this.videoCtx, this.videoCodec, (AVDictionary) null) < 0) {
            throw new IOException("Could not open video codec");
        }
        av_dump_format(this.formatCtx, 0, filename, 1);
    }

    @Override
    public void captureFrame() throws IOException {
        ByteBuffer frameBuffer = this.frame;
        int size = frameBuffer.limit() / 3;
        for (int i = 0; i < size; i++) {
            int val = (int) (this.frames + i);
            frameBuffer.put(3 * i, (byte) val);
            frameBuffer.put(3 * i + 1, (byte) (val >> 8));
            frameBuffer.put(3 * i + 2, (byte) (val >> 16));
        }
        av_frame_make_writable(this.codecFrame);
        PointerPointer<?> srcSlice = new PointerPointer<>(4);
        srcSlice.put(new Pointer(frameBuffer));
        IntPointer srcStride = new IntPointer(4);
        srcStride.put(this.stride);
        PointerPointer<?> dstSlice = this.codecFrame.data();
        IntPointer dstStride = this.codecFrame.linesize();
        sws_scale(this.swsContext,
                srcSlice, srcStride, 0, options.height,
                dstSlice, dstStride);
        this.codecFrame.pts(this.frames++);
        encodeFrame(this.codecFrame);
    }

    private void encodeFrame(AVFrame frame) throws IOException {
        int ret = avcodec_send_frame(this.videoCtx, frame);
        if (ret < 0) throw new IOException("Could not send frame to encoder");
        AVPacket packet = av_packet_alloc();
        while (true) {
            ret = avcodec_receive_packet(this.videoCtx, packet);
            if (ret == AVERROR_EAGAIN() || ret == AVERROR_EOF()) return;
            if (ret < 0) throw new IOException("Error encoding frame");
            av_packet_rescale_ts(packet, this.videoCtx.time_base(), this.videoStream.time_base());
            packet.stream_index(this.videoStream.index());
            int ret2 = av_interleaved_write_frame(this.formatCtx, packet);
            if (ret2 < 0) throw new IOException("Error writing frame");
        }
    }

    @Override
    public void close() throws IOException {
        encodeFrame(null);
        avcodec_close(this.videoCtx);
        av_frame_free(this.codecFrame);
        sws_freeContext(this.swsContext);
        av_write_trailer(this.formatCtx);
        av_dump_format(this.formatCtx, 0, filename, 1);
        avio_closep(this.formatCtx.pb());
        avformat_free_context(this.formatCtx);
    }

    public static class Options extends Encoder.Options {
        public Options(int width, int height, float frameRate) {
            super(width, height, frameRate);
        }
    }

    private static AVRational rational(int num, int den) {
        AVRational r = new AVRational();
        r.num(num);
        r.den(den);
        return r;
    }

    private static AVRational rational(float f) {
        if (f == 0) return rational(0, 1);
        if (f < 0) {
            AVRational r = rational(-f);
            r.num(-r.num());
            return r;
        }
        if (f < 1) return invert(rational(1 / f));
        int scale = 1;
        while (scale < Integer.MAX_VALUE / 2) {
            int num = Math.round(f * scale);
            float approx = (float) num / scale;
            float err = 1 - (approx > f ? f / approx : approx / f);
            if (err < 0.01f) return rational(num, scale);
            scale <<= 1;
        }
        return rational(Math.round(f * scale), scale);
    }

    private static AVRational invert(AVRational r) {
        int num = r.num();
        r.num(r.den());
        r.den(num);
        return r;
    }

    private static ByteBuffer aligned(int size, int alignment) {
        ByteBuffer buf = ByteBuffer.allocateDirect(size + alignment - 1);
        long address = ((DirectBuffer) buf).address();
        int mask = alignment - 1;
        buf.position((alignment - ((int) address & mask)) & mask);
        buf.limit(buf.position() + size);
        return buf.slice();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("libav version " + av_version_info().getString());
        av_log_set_level(AV_LOG_DEBUG);
        LibAvEncoder enc = new LibAvEncoder(new LibAvEncoder.Options(1280, 720, 60));
        enc.open();
        for (int i = 0; i < 300; i++) {
            enc.captureFrame();
        }
        enc.close();
    }
}
