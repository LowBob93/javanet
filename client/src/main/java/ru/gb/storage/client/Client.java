package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;
import ru.gb.storage.commons.message.AuthMessage;
import ru.gb.storage.commons.message.Message;
import ru.gb.storage.commons.message.TextMessage;
import ru.gb.storage.commons.message.DateMessage;

import java.util.Calendar;
import java.util.Date;


public class Client {

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {

        final NioEventLoopGroup group = new NioEventLoopGroup(1);

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sCh) throws Exception {
                            sCh.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message message) {
                                            System.out.println("receive massage: " + message);
                                        }
                                    }
                            );
                        }
                    });

            System.out.println("Client started");

            Channel channel = bootstrap.connect("localhost", 8080 ).sync().channel();


            AuthMessage auth = new AuthMessage();
            auth.setLogin("LokiCat");
            auth.setPassword("ObshiySalam");
            channel.write(auth);
            channel.flush();

            TextMessage textMessage = new TextMessage();
            textMessage.setText(String.format("[%s] %s", Calendar.getInstance(), Thread.currentThread().getName()));
            System.out.println("Try to send message: " + textMessage);
            channel.writeAndFlush(textMessage);

            DateMessage dateMessage = new DateMessage();
            dateMessage.setDate(new Date());
            System.out.println("Try to send message: " + dateMessage);
            channel.writeAndFlush(dateMessage);

            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}