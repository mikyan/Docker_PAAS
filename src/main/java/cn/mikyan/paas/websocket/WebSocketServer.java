package cn.mikyan.paas.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;

import cn.mikyan.paas.utils.JsonUtils;


/**
 * WebSocket服务器
 * @author jitwxs
 * @since 2018/7/9 16:08
 */
@ServerEndpoint(value = "/ws/{userId}")
@Component
public class WebSocketServer {

    private static HashMap<String, Session> webSocketSet = new HashMap<>();

    private final String key = "session";

    private final String ID_PREFIX = "UID:";

    private Session session;

    FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {

        this.session = session;
        webSocketSet.put(session.getId(), session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        webSocketSet.remove(this.session.getId());
    }

    /**
     * 连接出错调用的方法
     */
    @OnError
    public void onError(Throwable error) {
        //log.error("WebSocket连接出错");
    }

    @OnMessage
    public void onMessage(String message) {
//        log.info("{}({})：{}",this.session.getId(), format.format(new Date()), message);
        try {
            Map<String, String> map = new HashMap<>(16);
            map.put("info", "heart");
            sendMessage(JsonUtils.objectToJson(map), this.session.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     * @author jitwxs
     * @since 2018/7/9 16:55
     */
    public void sendMessage(String message, String sessionId) throws IOException {
        Session session = webSocketSet.get(sessionId);
        if(session != null) {
            session.getBasicRemote().sendText(message);
        } else {
            throw new IOException("WebSocket连接中断");
        }
    }
}
