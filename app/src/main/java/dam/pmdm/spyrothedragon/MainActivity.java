package dam.pmdm.spyrothedragon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.ArrowBelowBinding;
import dam.pmdm.spyrothedragon.databinding.FinalizedBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBubbleBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBubble2Binding;
import dam.pmdm.spyrothedragon.databinding.GuideBubble3Binding;
import dam.pmdm.spyrothedragon.databinding.GuideBubble4Binding;
import dam.pmdm.spyrothedragon.databinding.FondoBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GuideBinding guideBinding;
    private FondoBinding fondoBinding;
    private NavController navController;
    private boolean needToStartGuide = true;
    private ArrowBelowBinding arrowBelowBinding;
    private GuideBubbleBinding guideBubbleBinding;
    private GuideBubble2Binding guideBubble2Binding;
    private GuideBubble3Binding guideBubble3Binding;
    private GuideBubble4Binding guideBubble4Binding;
    private FinalizedBinding finalizedBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            // Infla el layout principal usando ViewBinding
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Intentamos configurar el Toolbar con try-catch

            // Inicializa el Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);  // Verifica que el ID sea correcto
            setSupportActionBar(toolbar); // Establece el Toolbar como ActionBar


            // Accede al binding de la guía (layout include)
            guideBinding = binding.includeLayout;
            finalizedBinding = binding.excludeLayout;
            guideBubbleBinding = binding.guideBubble;
            fondoBinding = binding.guideBackLayout;
            arrowBelowBinding = binding.arrowBelow;
            guideBubble2Binding = binding.guideBubble2;
            guideBubble3Binding = binding.guideBubble3;
            guideBubble4Binding = binding.guideBubble4;


            // Configura el NavHostFragment y el NavController
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
            if (navHostFragment != null) {
                navController = NavHostFragment.findNavController(navHostFragment);
                NavigationUI.setupWithNavController(binding.navView, navController);
                NavigationUI.setupActionBarWithNavController(this, navController);
            }

            binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

            // Cambia la visibilidad de la flecha de regreso según la pantalla actual
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.navigation_characters ||
                        destination.getId() == R.id.navigation_worlds ||
                        destination.getId() == R.id.navigation_collectibles) {
                    // En las pestañas principales, no mostramos la flecha de atrás
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    // En otras pantallas, se muestra la flecha de regreso
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            });

            // Configura el botón de "Saltar guía"
            binding.exitGuide.setOnClickListener(this::onExitGuide);

            // Configura el botón "Comenzar" de la guía
            guideBinding.startButton.setOnClickListener(v -> {

                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.start_sound);
                mediaPlayer.start();


                mediaPlayer.setOnCompletionListener(mp -> mp.release());

                // Oculta la guía y desbloquea el Drawer
                needToStartGuide = false;
                guideBinding.guideLayout.setVisibility(View.GONE);
                showGuideBubble();
            });

            finalizedBinding.botonFinalizar.setOnClickListener(this::onExitGuide);

            binding.step1Guide.setOnClickListener(v -> {
                LinearLayout guideBubble = (LinearLayout) guideBubbleBinding.guideBubble;
                binding.step1Guide.setVisibility(View.GONE);
                guideBubble.setVisibility(View.GONE);
                arrowBelowBinding.arrowBelow.setVisibility(View.GONE);
                showGuideBubble2();
            });

            binding.step2Guide.setOnClickListener(v -> {
                LinearLayout guideBubble2 = (LinearLayout) guideBubble2Binding.guide2Bubble;
                binding.step2Guide.setVisibility(View.GONE);
                guideBubble2.setVisibility(View.GONE);
                arrowBelowBinding.arrowBelow.setVisibility(View.GONE);
                showGuideBubble3();
            });

            binding.step3Guide.setOnClickListener(v -> {
                LinearLayout guideBubble3 = (LinearLayout) guideBubble3Binding.guide3Bubble;
                binding.step3Guide.setVisibility(View.GONE);
                guideBubble3.setVisibility(View.GONE);
                arrowBelowBinding.arrowBelow.setVisibility(View.GONE);
                showGuideBubble4();
            });

            binding.step4Guide.setOnClickListener(v -> {
                LinearLayout guideBubble4 = (LinearLayout) guideBubble4Binding.guide4Bubble;
                binding.step4Guide.setVisibility(View.GONE);
                guideBubble4.setVisibility(View.GONE);
                arrowBelowBinding.arrowBelow.setVisibility(View.GONE);
                finalizedGuide();
            });


            // Inicializa la guía
            initializeGuide();


        } catch (Exception e) {
            // Captura cualquier excepción y muestra un mensaje de error
            Log.e("MainActivity", "Error al configurar el Toolbar", e);
            Toast.makeText(this, "Error al configurar el Toolbar", Toast.LENGTH_SHORT).show();
        }
    }


    private void showGuideBubbleInternal(View stepButton, View guideBubble, int navItemId, int yOffset, int arrowRotation, float arrowScaleY, int arrowYOffset) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            stepButton.setVisibility(View.VISIBLE);
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            fondoBinding.fondoLayout.setVisibility(View.VISIBLE);

            guideBubble.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int bubbleWidth = guideBubble.getMeasuredWidth();
            int bubbleHeight = guideBubble.getMeasuredHeight();

            View button = binding.navView.findViewById(navItemId);
            int[] location = new int[2];
            button.getLocationOnScreen(location);
            int buttonX = location[0];
            int buttonY = location[1];

            int iconCenterX = buttonX + (button.getWidth() / 2);
            int iconCenterY = buttonY - bubbleHeight - yOffset;

            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            if (iconCenterY < 0) {
                iconCenterY = buttonY + button.getHeight() + 20;
            } else if (iconCenterY + bubbleHeight > screenHeight) {
                iconCenterY = screenHeight - bubbleHeight - 100;
            }

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            if (iconCenterX - (bubbleWidth / 2) < 0) {
                iconCenterX = bubbleWidth / 2;
            } else if (iconCenterX + (bubbleWidth / 2) > screenWidth) {
                iconCenterX = screenWidth - (bubbleWidth / 2);
            }

            guideBubble.setX(iconCenterX - (bubbleWidth / 2));
            guideBubble.setY(iconCenterY);

            if (arrowBelowBinding != null) {
                arrowBelowBinding.arrowBelow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int arrowWidth = arrowBelowBinding.arrowBelow.getMeasuredWidth();
                int arrowHeight = arrowBelowBinding.arrowBelow.getMeasuredHeight();

                int arrowX = iconCenterX - (arrowWidth / 2);
                int arrowY = iconCenterY + bubbleHeight + arrowYOffset;

                arrowBelowBinding.arrowBelow.setX(arrowX);
                arrowBelowBinding.arrowBelow.setY(arrowY);
                arrowBelowBinding.arrowBelow.setRotation(arrowRotation);
                arrowBelowBinding.arrowBelow.setScaleY(arrowScaleY);
                arrowBelowBinding.arrowBelow.setVisibility(View.VISIBLE);
            }

            guideBubble.setVisibility(View.VISIBLE);
            Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.bubble_enter);
            guideBubble.startAnimation(enterAnimation);
        });
    }

    private void showGuideBubble() {
        showGuideBubbleInternal(binding.step1Guide, guideBubbleBinding.guideBubble, R.id.nav_characters, 300, 12, 1f, 0);
    }

    private void showGuideBubble2() {
        showGuideBubbleInternal(binding.step2Guide, guideBubble2Binding.guide2Bubble, R.id.nav_worlds, 300, 0, 0.8f, -50);
    }

    private void showGuideBubble3() {
        showGuideBubbleInternal(binding.step3Guide, guideBubble3Binding.guide3Bubble, R.id.nav_collectibles, 300, -25, 1.1f, 0);
    }

    private void showGuideBubble4() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            binding.step4Guide.setVisibility(View.VISIBLE);


            LinearLayout guide4Bubble = (LinearLayout) guideBubble4Binding.guide4Bubble;


            BottomNavigationView bottomNav = binding.navView;


            guide4Bubble.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int bubbleWidth = guide4Bubble.getMeasuredWidth();
            int bubbleHeight = guide4Bubble.getMeasuredHeight();


            View button = bottomNav.findViewById(R.id.nav_worlds);
            int[] location = new int[2];
            button.getLocationOnScreen(location);
            int buttonX = location[0];
            int buttonY = location[1];


            int iconCenterX = buttonX + (button.getWidth() / 2);
            iconCenterX += 100;


            int iconCenterY = buttonY - bubbleHeight - 1700;


            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            if (iconCenterY < 0) {

                iconCenterY = buttonY + button.getHeight() + 20;
            } else if (iconCenterY + bubbleHeight > screenHeight) {

                iconCenterY = screenHeight - bubbleHeight - 100;
            }


            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            if (iconCenterX - (bubbleWidth / 2) < 0) {

                iconCenterX = bubbleWidth / 2;
            } else if (iconCenterX + (bubbleWidth / 2) > screenWidth) {

                iconCenterX = screenWidth - (bubbleWidth / 2);
            }


            guide4Bubble.setX(iconCenterX - (bubbleWidth / 2));
            guide4Bubble.setY(iconCenterY);


            if (arrowBelowBinding != null) {

                arrowBelowBinding.arrowBelow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int arrowWidth = arrowBelowBinding.arrowBelow.getMeasuredWidth();
                int arrowHeight = arrowBelowBinding.arrowBelow.getMeasuredHeight();


                int arrowX = iconCenterX - (arrowWidth / 2) + 175;


                int arrowY = (iconCenterY - arrowHeight) + 85;

                arrowBelowBinding.arrowBelow.setX(arrowX);
                arrowBelowBinding.arrowBelow.setY(arrowY);


                arrowBelowBinding.arrowBelow.setRotation(-120);


                arrowBelowBinding.arrowBelow.setVisibility(View.VISIBLE);
            }


            guide4Bubble.setVisibility(View.VISIBLE);


            Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.bubble_enter);
            guide4Bubble.startAnimation(enterAnimation);
        });
    }

    private void initializeGuide() {
        // Obtener SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Comprobar si 'ISFIRSTTIME' existe
        boolean isFirstTime = sharedPreferences.getBoolean("ISFIRSTTIME", true);


        // Si es la primera vez, mostramos la guía
        if (true) {
            // Guardamos 'false' para que no se muestre la guía en el futuro
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ISFIRSTTIME", false);
            editor.apply();
            if (needToStartGuide) {

                binding.exitGuide.setVisibility(View.VISIBLE);
                // Bloquea el Drawer para evitar interacción mientras se muestra la guía
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                // Muestra la guía (que, al tener match_parent, cubrirá todo el contenido)
                guideBinding.guideLayout.setVisibility(View.VISIBLE);

                // Animación de desvanecimiento para el texto de bienvenida
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(guideBinding.textStep, "alpha", 0f, 1f);
                fadeIn.setDuration(1000);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(fadeIn);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (needToStartGuide) {
                            super.onAnimationEnd(animation);
                            // Muestra el bocadillo en la segunda pantalla
                        }
                    }
                });
            }
        }
    }


    private void finalizedGuide() {
        fondoBinding.fondoLayout.setVisibility(View.GONE);
        // Muestra la guía (que, al tener match_parent, cubrirá todo el contenido)
        finalizedBinding.guideExclude.setVisibility(View.VISIBLE);
        binding.exitGuide.setVisibility(View.GONE);

        // Animación de desvanecimiento para el texto de bienvenida
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(finalizedBinding.textStep5, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn);
        animatorSet.start();
    }

    /**
     * Método invocado al hacer clic en el botón "Saltar guía".
     * Oculta la guía y desbloquea el Drawer.
     */
    private void onExitGuide(View view) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.end_sound);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            // Liberar recursos del MediaPlayer
            mp.release();

            // Ocultar elementos visuales después de que el sonido haya terminado
            hideViews(
                    guideBinding.guideLayout,
                    binding.exitGuide,
                    fondoBinding.fondoLayout,
                    arrowBelowBinding.arrowBelow,
                    binding.step1Guide,
                    binding.step2Guide,
                    binding.step3Guide,
                    binding.step4Guide,
                    guideBubbleBinding.guideBubble,
                    guideBubble2Binding.guide2Bubble,
                    guideBubble3Binding.guide3Bubble,
                    guideBubble4Binding.guide4Bubble,
                    finalizedBinding.guideExclude
            );

            // Cerrar el drawer y desbloquear
            binding.drawerLayout.close();
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        });
    }


    // Método auxiliar para ocultar vistas
    private void hideViews(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }


    /**
     * Gestiona la navegación según el ítem seleccionado en el BottomNavigationView.
     */
    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters) {
            navController.navigate(R.id.navigation_characters);
        } else if (menuItem.getItemId() == R.id.nav_worlds) {
            navController.navigate(R.id.navigation_worlds);
        } else {
            navController.navigate(R.id.navigation_collectibles);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú de información
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona la acción del menú de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Muestra un diálogo con información sobre la aplicación.
     */
    private void showInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }
}
