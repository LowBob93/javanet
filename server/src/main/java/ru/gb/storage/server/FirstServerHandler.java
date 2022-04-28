package ru.gb.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.commons.message.*;


public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New Active channel");
        TextMessage answer = new TextMessage();
        answer.setText("Successfully connection");
        ctx.writeAndFlush(answer);
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message message) {

        if (message instanceof TextMessage) {
            TextMessage msg = (TextMessage) message;
            System.out.println(msg.getText());
            ctx.writeAndFlush(message);
        }

        if (message instanceof DateMessage) {
            DateMessage msg = (DateMessage) message;
            System.out.println(msg.getDate());
            ctx.writeAndFlush(message);
        }

        if (message instanceof AuthMessage) {
            AuthMessage msg = (AuthMessage) message;
            TextMessage resultMessage = new TextMessage();
            if(((AuthMessage) message).getLogin().equals("LokiCat") && ((AuthMessage) message).getPassword().equals("ObshiySalam")){
                System.out.println("");
                resultMessage.setText("Authentication success");
            } else {
                System.out.println("Wrong login or password");
                resultMessage.setText("Wrong login or password");
            }
            ctx.writeAndFlush(resultMessage);
        }
    }
}

