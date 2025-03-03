package dam.pmdm.spyrothedragon.animations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

public class FireAnimation extends View {

    private Paint paint;
    private Path path;
    private float fireWidth = 0;
    private float fireHeight = 150; // Aumentar la altura
    private float fireBaseX;
    private float fireBaseY; // Añadir fireBaseY

    private Handler handler = new Handler();
    private Runnable animationRunnable;

    public FireAnimation(Context context) {
        super(context);
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        // Ajustar el tamaño para la llama horizontal
        setLayoutParams(new ViewGroup.LayoutParams(400, (int) fireHeight)); // Aumentar el ancho
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fireBaseX = 10;
        fireBaseY = h - fireHeight + 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dibujarLlama(canvas);
        animateFire();
        invalidate();
    }

    private void dibujarLlama(Canvas canvas) {
        float x = fireBaseX + fireWidth;
        float y = fireBaseY + fireHeight / 2;


        int[] colors = {
                Color.argb(200, 255, 200, 0),
                Color.argb(230, 255, 100, 0),
                Color.RED
        };
        LinearGradient gradiente = new LinearGradient(fireBaseX, y, x, y, colors, null, Shader.TileMode.CLAMP);

        paint.setShader(gradiente);
        paint.setStyle(Paint.Style.FILL);

        path.reset();
        path.moveTo(fireBaseX, y);
        path.quadTo(x - fireWidth / 4, y - fireHeight / 3, x, y);
        path.quadTo(x - fireWidth / 4, y + fireHeight / 3, fireBaseX, y);
        canvas.drawPath(path, paint);


        int[] colorsSuperior = {
                Color.argb(220, 255, 150, 0),
                Color.argb(150, 255, 255, 255)
        };
        LinearGradient gradienteSuperior = new LinearGradient(x, y, x + fireWidth / 4, y, colorsSuperior, null, Shader.TileMode.CLAMP);

        paint.setShader(gradienteSuperior);
        path.reset();
        path.moveTo(x, y);
        path.quadTo(x + fireWidth / 4, y, x, y);
        canvas.drawPath(path, paint);
    }

    private void animateFire() {
        fireWidth += 8;
        if (fireWidth > 250) {
            fireWidth = 0;
        }
    }

    public void start(ViewGroup container) {
        if (container != null) {
            if (getParent() != null) {
                ((ViewGroup) getParent()).removeView(this);
            }


            container.addView(this);


            animationRunnable = new Runnable() {
                @Override
                public void run() {
                    invalidate();
                    handler.postDelayed(this, 30);
                }
            };
            handler.post(animationRunnable);

            handler.postDelayed(() -> cleanup(), 5000);
        }
    }

    public void cleanup() {
        handler.removeCallbacks(animationRunnable);
        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
    }
}
