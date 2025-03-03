package dam.pmdm.spyrothedragon.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.animations.FireAnimation;

import android.util.Log;

import java.util.List;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private List<Character> list;

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {
        try {
            Character character = list.get(position);
            holder.nameTextView.setText(character.getName());

            int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
            holder.imageImageView.setImageResource(imageResId);

            if (character.getName().equals("Spyro")) {
                holder.imageImageView.setOnLongClickListener(v -> {
                    Toast.makeText(v.getContext(), "¡Llama de fuego activada!", Toast.LENGTH_SHORT).show();

                    FireAnimation fireAnimation = new FireAnimation(v.getContext());

                    FrameLayout animationContainer = holder.itemView.findViewById(R.id.animationContainer);

                    // Verifica si ya hay una animación y elimínala
                    if (animationContainer != null && animationContainer.getChildCount() > 0) {
                        animationContainer.removeAllViews(); // Elimina todas las vistas hijas
                    }

                    // Añade la animación al FrameLayout
                    if (animationContainer != null) {
                        animationContainer.addView(fireAnimation);
                        fireAnimation.start(animationContainer);
                    } else {
                        Log.e("CharactersAdapter", "animationContainer no encontrado");
                    }

                    return true;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(holder.itemView.getContext(), "Error al vincular los datos del personaje", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}