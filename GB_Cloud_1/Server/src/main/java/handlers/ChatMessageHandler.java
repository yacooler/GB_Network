package handlers;

import client.userCredential.Record;
import database.BaseAuthService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;

import java.util.Arrays;
import java.util.List;

@Log4j
public class ChatMessageHandler extends SimpleChannelInboundHandler<String> {
    private final BaseAuthService baseAuthService = new BaseAuthService();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(String.format("User connected: %s", ctx.channel().localAddress()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        if (s.length() > 0) {
            if (s.equals("User connection")) {
                channelHandlerContext.writeAndFlush("ConnectionTrue");
            } else if (s.equals("/end")) {
                channelHandlerContext.writeAndFlush("/end");
            } else if (s.equals("getUserCredential")) {
                channelHandlerContext.writeAndFlush(String.format("UserCredential|%s|%s|%s", Record.getInstance().getId(),
                        Record.getInstance().getLogin(), Record.getInstance().getPassword()));
            } else {
                String[] messageShare = s.split("\\|");
                if (messageShare[0].equals("Auth")) {
                    channelHandlerContext.writeAndFlush(baseAuthService.findUser(messageShare[1], messageShare[2]));
                } else if (messageShare[0].equals("Registration")) {
                    channelHandlerContext.writeAndFlush(baseAuthService.userRegistration(messageShare[1], messageShare[2]));
                } else if (messageShare[0].equals("UploadMusic")) {
                    channelHandlerContext.writeAndFlush(baseAuthService.uploadMusic(messageShare[1], messageShare[2].getBytes()));
                } else if (messageShare[0].equals("DownloadMusicNames")) {
                    List<String> musicNames = baseAuthService.downloadMusicName(Integer.parseInt(messageShare[1]));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("musicNamesDownloaded|");
                    musicNames.forEach(s1 -> stringBuilder.append(s1).append("|"));
                    channelHandlerContext.writeAndFlush(stringBuilder);
                } else if (messageShare[0].equals("DownloadSingleMusic")) {
                    channelHandlerContext.writeAndFlush("DownloadSingleMusic|" + Arrays.toString(baseAuthService.downloadSingleMusic(Integer.parseInt(messageShare[1]), messageShare[2])));
                }
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info(String.format("User disconnected: %s", ctx.channel().localAddress()));
    }
}
