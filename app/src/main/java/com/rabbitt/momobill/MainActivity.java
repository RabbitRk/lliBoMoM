package com.rabbitt.momobill;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.rabbitt.momobill.fragment.ClientFrag;
import com.rabbitt.momobill.fragment.DashFrag;
import com.rabbitt.momobill.fragment.InventoryFrag;
import com.rabbitt.momobill.fragment.InvoiceFrag;
import com.rabbitt.momobill.fragment.OrderFrag;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "maluHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        loadFragment(new DashFrag());

        BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                Log.i(TAG, "onNavigationChanged: "+view.getId()+"  "+position);
                switch (position)
                {
                    case 0:
                        toolbar.setTitle(getString(R.string.home));
                        loadFragment(new DashFrag());
                        break;
                    case 1:
                        toolbar.setTitle("Inventory");
                        loadFragment(new InventoryFrag());
                        break;
                    case 2:
                        toolbar.setTitle("Invoice");
                        loadFragment(new InvoiceFrag());
                        break;
                    case 3:
                        toolbar.setTitle("Order");
                        loadFragment(new OrderFrag());
                        break;
                    case 4:
                        toolbar.setTitle("Clients");
                        loadFragment(new ClientFrag());
                        break;
                }
            }
        });
    }

    public Boolean loadFragment(final Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            Log.i(TAG, "Current thread: " + Thread.currentThread().getId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentcontainer, fragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }

        return false;
    }
}