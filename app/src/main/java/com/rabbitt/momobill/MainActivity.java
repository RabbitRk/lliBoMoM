package com.rabbitt.momobill;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rabbitt.momobill.fragment.ClientFrag;
import com.rabbitt.momobill.fragment.DashFrag;
import com.rabbitt.momobill.fragment.InventoryFrag;
import com.rabbitt.momobill.fragment.InvoiceFrag;
import com.rabbitt.momobill.fragment.OrderFrag;
import com.rabbitt.momobill.prefsManager.PrefsManager;

import static com.rabbitt.momobill.prefsManager.PrefsManager.OWNER;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PREF;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "maluHomeActivity";
    Boolean bool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrefsManager prefsManager = new PrefsManager(this);
        prefsManager.setFirstTimeLaunch(true);

        SharedPreferences shrp = getSharedPreferences(USER_PREF, MODE_PRIVATE);
        bool = shrp.getBoolean(OWNER,true);

        Log.i(TAG, "onCreate: Boolean:  "+shrp.getBoolean(OWNER,false));
        final Toolbar toolbar = findViewById(R.id.toolbar);

        if (bool){
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
        else
        {
            toolbar.setTitle("Order");
            loadFragment(new OrderFrag());
        }


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

    @Override
    public void onBackPressed() {
        BubbleNavigationLinearView bottomNavigationView = findViewById(R.id.bottom_navigation_view_linear);
        int selectedItemId = bottomNavigationView.getCurrentActiveItemPosition();
        Log.i(TAG, "onBackPressed: "+selectedItemId);
//
//        if (getCurrentFragment().equals(null))
//        {
//            Log.i(TAG, "onBackPressed: in");
//            if (R.id.navigation_home != selectedItemId) {
//                setHomeItem(HomeScreen.this);
//            } else {
//                finish();
//                super.onBackPressed();
//            }
//        }
        try
        {
//            String com = prefsManager.getIns();
//            Log.i(TAG, "onBackPressed: "+com+"        "+getCurrentFragment());
//            Log.i(TAG, "rabbitt: "+adapter.getCategory());
//            switch (getCurrentFragment())
//            {
//                //normal products
//                case "ProductFragment":
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.fragment_container, new ProductFragment());
//                    ft.addToBackStack(null);
//                    ft.commit();
////                    Toast.makeText(this, "ProdFrag", Toast.LENGTH_SHORT).show();
//                    break;
//                case "CompanyFragment":
//                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                    ft1.replace(R.id.fragment_container, CompanyListing.newInstance(com));
//                    ft1.addToBackStack("ProductFragment");
//                    ft1.commit();
////                    Toast.makeText(this, "ComFrag", Toast.LENGTH_SHORT).show();
//                    break;
//                //normal products end
//
//                case "SubCategory":
//                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
//                    ft2.replace(R.id.fragment_container, SubCategoryListing.newInstance(com));
//                    ft2.addToBackStack("ProductFragment");
//                    ft2.commit();
////                    Toast.makeText(this, "subcatFrag", Toast.LENGTH_SHORT).show();
//                    break;
//
//                case "SubCompany":
//                    String sub = prefsManager.getSubIns();
//                    String subC = prefsManager.getSubCatIns();
//                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
//                    ft3.replace(R.id.fragment_container, SubCompanyListing.newInstance(subC,sub));
//                    ft3.addToBackStack("SubCategory");
//                    ft3.commit();
////                    Toast.makeText(this, "subCompany", Toast.LENGTH_SHORT).show();
//                    break;
////                case "SubProduct":
////                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
////                    ft4.replace(R.id.fragment_container, SubProductListing.newInstance(com,com,com));
////                    ft4.addToBackStack("SubCompany");
////                    ft4.commit();
////                    Toast.makeText(this, "SubProduct", Toast.LENGTH_SHORT).show();
////                    break;
//
//                default:
                    if (R.id.l_item_home != selectedItemId) {
                        setHomeItem(MainActivity.this);
                    } else {
                        finish();
                        super.onBackPressed();
                    }
//            }
        }
        catch (NullPointerException npe)
        {
            Log.i(TAG, "onBackPressed:Exception "+npe.getMessage());

            if (selectedItemId != 0) {
                setHomeItem(MainActivity.this);
            } else {
                finish();
                super.onBackPressed();
            }
        }

    }

    public static void setHomeItem(Activity activity) {
        BubbleNavigationLinearView bottomNavigationView = activity.findViewById(R.id.bottom_navigation_view_linear);
        bottomNavigationView.setCurrentActiveItem(0);
    }
}