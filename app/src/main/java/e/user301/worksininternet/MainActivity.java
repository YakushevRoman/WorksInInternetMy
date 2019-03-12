package e.user301.worksininternet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private FragmentManager rFragmentManager;
    private Fragment rFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rFragmentManager = getSupportFragmentManager();
        rFragment = rFragmentManager.findFragmentById(R.id.main_container);
        if (rFragment == null){
            rFragment = new PhotoGalleryFragment();
            rFragmentManager
                    .beginTransaction()
                    .add(R.id.main_container, rFragment)
                    .commit();
        }

    }
}
