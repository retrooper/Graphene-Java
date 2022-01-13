package com.github.graphene.handler.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import java.util.List;

public class PacketDecryptionHandler extends ByteToMessageDecoder {
    private final Cipher cipher;
    private byte[] heap = new byte[0];

    public PacketDecryptionHandler(@Nullable Cipher cipher) {
        this.cipher = cipher;
    }

    private byte[] asBytes(ByteBuf buffer) {
        int len = buffer.readableBytes();
        if (this.heap.length < len)
            this.heap = new byte[len];
        buffer.readBytes(heap, 0, len);
        return heap;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (cipher != null) {
            int len = in.readableBytes();
            byte[] bytes = asBytes(in);
            ByteBuf buffer = ctx.alloc().heapBuffer(cipher.getOutputSize(len));
            buffer.writerIndex(cipher.update(bytes, 0, len, buffer.array(), buffer.arrayOffset()));
            out.add(buffer);
        }
        else {
            out.add(in);
        }
    }
}
