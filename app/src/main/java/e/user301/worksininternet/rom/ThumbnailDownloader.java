package e.user301.worksininternet.rom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import e.user301.worksininternet.FlickrFetchr;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    // хранит ссылку на обьект
    private Handler rRequestHandler;
    // безопастная мапа в отношении потоков
    private ConcurrentMap <T, String> rRequestMap = new ConcurrentHashMap<>();
    //
    private Handler rResponseHandler;
    //
    private ThumbnailDownloaderListener <T> rTThumbnailDownloaderListener;
    //
    public interface ThumbnailDownloaderListener<T>{
        // будет вызван когда изображения полностью получено
        void onThumbnailDownloader(T target, Bitmap bitmap);
    }



    public void setrTThumbnailDownloaderListener(ThumbnailDownloaderListener<T> rTThumbnailDownloaderListener) {
        this.rTThumbnailDownloaderListener = rTThumbnailDownloaderListener;
    }

    public ThumbnailDownloader(Handler rResponseHandler) {
        super(TAG);
        this.rResponseHandler = rResponseHandler;
    }

    public void queueThumbnail (T target, String url){
        Log.i(TAG, "queueThumbnail: " + url);

        if (url == null){
            rRequestMap.remove(target);
        }else{
            rRequestMap.put(target,url);
            rRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                .sendToTarget();
        }
    }

    @Override
    protected void onLooperPrepared() {
        rRequestHandler = new Handler(){
            // когда сообщение извлечено из очереди и готово к обработки
            @Override
            public void handleMessage(Message msg) {
                // проверям тип сообщения
                if (msg.what == MESSAGE_DOWNLOAD){
                    // читаем значения
                    T target = (T) msg.obj;
                    Log.d(TAG, "handleMessage:  for url : " +
                        rRequestMap.get(target));
                    handleRequest (target);
                }
            }
        };
    }
    // загрузка
    private void handleRequest(final T target) {
        final String url = rRequestMap.get(target);
        // проверям url
        if (url == null){
            return;
        }
        // используем bitmap для построения растрового изображения с массива байт полученным
        try {
            byte[] bytemap = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bytemap, 0, bytemap.length);
            Log.d(TAG, "handleRequest: Bit map");

            rResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (rRequestMap.get(target) != url){
                        return;
                    }

                    rRequestMap.remove(target);
                    rTThumbnailDownloaderListener.onThumbnailDownloader(target, bitmap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearQueue (){
        rRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
