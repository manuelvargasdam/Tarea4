package dam.pmdm.spyrothedragon;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_video);

            VideoView videoView = findViewById(R.id.videoView);
            String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.easter_egg_video;
            Uri uri = Uri.parse(videoPath);
            videoView.setVideoURI(uri);

            videoView.setOnPreparedListener(mp -> {
                Log.d(TAG, "Video preparado, iniciando reproducción...");
                videoView.start();
            });

            videoView.setOnCompletionListener(mp -> {
                Log.d(TAG, "Video finalizado, cerrando actividad...");
                finish();
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Error en reproducción: what=" + what + ", extra=" + extra);
                return true; // Indicar que el error fue manejado
            });

        } catch (Exception e) {
            Log.e(TAG, "Error al cargar el video", e);
        }
    }
}
