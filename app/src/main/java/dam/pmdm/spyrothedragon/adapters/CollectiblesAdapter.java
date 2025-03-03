package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.VideoActivity;
import android.app.Activity;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;
    private int clickCount = 0;
    private Handler handler = new Handler();
    private static final int CUATRO_TOQUES = 4;
    private static final int TIMEOUT = 2000; // 2 segundos

    public CollectiblesAdapter(List<Collectible> collectibleList) {
        this.list = collectibleList;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(
                collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName()
        );
        holder.imageImageView.setImageResource(imageResId);

        // Reiniciar cualquier clickListener heredado
        holder.itemView.setOnClickListener(null);

        // Detectar los clics en el ítem "Gemas"
        if (collectible.getName().equalsIgnoreCase("Gemas")) {
            holder.itemView.setOnClickListener(view -> {
                clickCount++;

                if (clickCount == 1) {
                    handler.postDelayed(() -> clickCount = 0, TIMEOUT);
                }

                if (clickCount >= CUATRO_TOQUES) {
                    clickCount = 0;

                    Context context = view.getContext();
                    Intent intent = new Intent(context, VideoActivity.class);

                    // Iniciar la nueva actividad
                    context.startActivity(intent);

                    if (context instanceof Activity) {
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                    }
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
