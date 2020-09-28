package cn.mikyan.paas.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 输出线程
 * 使用线程输出，方式流等待卡住
 * @author jitwxs
 * @since 2018/7/1 14:23
 */
public class OutPutThread extends Thread {
    private InputStream inputStream;
    private WebSocketSession session;

    public OutPutThread(InputStream inputStream, WebSocketSession session){
        super("OutPut"+ System.currentTimeMillis());
        this.session=session;
        this.inputStream=inputStream;
    }

    @Override
    public void run() {
        try{
            /* byte[] bytes=new byte[1024];
            while(!this.isInterrupted()){
                int n=inputStream.read(bytes);

                // 当n=-1时，代表连接关闭，例如用户输入exit
                if( n == -1) {
                    session.close();
                    return;
                }

                String msg=new String(bytes,0,n);
                session.sendMessage(new TextMessage(msg));
                bytes=new byte[1024];
            } */
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line=null; 
            while((line = reader.readLine()) != null) {
                System.out.println("---------");
                System.out.println("in:"+line);
                if(line.length()==0){
                    session.sendMessage(new TextMessage("\n")); 
                }else{
                    session.sendMessage(new TextMessage(line));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
