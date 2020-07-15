package com.rabbitt.momobill.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.ClientActivity;
import com.rabbitt.momobill.activity.EditClientActivity;
import com.rabbitt.momobill.adapter.ClientAdapter;
import com.rabbitt.momobill.model.Client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClientFrag extends Fragment implements View.OnClickListener, ClientAdapter.OnRecyleItemListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "maluClientFrag";

    public static List<Client> data = new ArrayList<>();
    private ClientAdapter clientAdapter;
    public static RecyclerView clientRecycler;
    private String mParam1;
    private String mParam2;

    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

    public static List<String> idList = new ArrayList<>();
    public static View.OnClickListener myOnClickListener;

    public ClientFrag() {
        // Required empty public constructor
    }

    public static ClientFrag newInstance(String param1, String param2) {
        ClientFrag fragment = new ClientFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_client, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View view) {
        //Initialization and Declaration
        FloatingActionButton fab = view.findViewById(R.id.fab_client_add);
        clientRecycler = view.findViewById(R.id.recycler_client);

        //Onclick listener
        fab.setOnClickListener(this);

        myOnClickListener = new MyOnClickListener(getContext());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Client");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: "+dataSnapshot);
                if (data != null) {
                    data.clear();
                    idList.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Client client = snapshot.getValue(Client.class);
                    data.add(client);
                    idList.add(snapshot.getKey());
                }
                updateRecycler(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateRecycler(List<Client> data) {
        //Update the recycler view
        clientAdapter = new ClientAdapter(data, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(getActivity());
        clientRecycler.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        clientRecycler.setItemAnimator(new DefaultItemAnimator());
        clientRecycler.setAdapter(clientAdapter);
        clientAdapter.notifyDataSetChanged();
        clientRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(getActivity(), "FAB Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), ClientActivity.class));
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
            return false;
        }

        return true;
    }


    @Override
    public void OnCall(int position) {

        if (checkPermission()) {
            Log.i(TAG, "OnItemClick: " + position);
            Log.i(TAG, "pos " + position);
            Client model = data.get(position);

            String number = model.getPhone();

            try {
                if (!number.equals("")) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(Intent.createChooser(callIntent, null));
                }
            } catch (Exception ex) {
                Log.i(TAG, "makeCall: " + ex.getMessage());
            }

            Log.i(TAG, "pos " + data);
        } else {
            Toast.makeText(getContext(), "Call Permission is Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnText(int position) {
        Log.i(TAG, "OnItemClick: "+position);
        Log.i(TAG, "pos " + position);
        Client model = data.get(position);

        String number = model.getPhone();

        boolean isWhatsappInstalled = whatsappInstalledOrNot();

        if (isWhatsappInstalled) {
            Uri mUri = Uri.parse("smsto:" + number);
            Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
            mIntent.setPackage("com.whatsapp");
            mIntent.putExtra("sms_body", "The text goes here");
            mIntent.putExtra("chat", true);
            startActivity(Intent.createChooser(mIntent, ""));
        } else {
            Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        }
    }

    @Override
    public void OnMail(int position) {
        Log.i(TAG, "OnItemClick: "+position);
        Log.i(TAG, "pos " + position);
        Client model = data.get(position);

        String emailaddress = model.getEmail();

        try {
            if (!emailaddress.equals("")) {
                Intent emailintent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", emailaddress, null));
                emailintent.putExtra(Intent.EXTRA_SUBJECT, "Santha Agency");
                startActivity(Intent.createChooser(emailintent, null));
            }
        } catch (Exception ex) {
            Log.i(TAG, "makeEmail: " + ex.getMessage());
        }
    }

    private boolean whatsappInstalledOrNot() {
        PackageManager pm = requireActivity().getPackageManager();
        boolean app_installed;// = false;
        try {
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;
        int selectedItemPosition;
        String clientid;
        String line;
        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            selectedItemPosition = clientRecycler.getChildPosition(v);
            clientid = idList.get(selectedItemPosition);
            showPopup(v);
        }
        public void showPopup(View view) {
            PopupMenu popup = new PopupMenu(context, view);
            try {
                // Reflection apis to enforce show icon
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals(POPUP_CONSTANT)) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            popup.getMenuInflater().inflate(R.menu.client_popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.delete:
                            deleteClient(clientid);
//                            Toast.makeText(context, "You clicked delete on "+data.get(selectedItemPosition).getName(), Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.edit:
                            editClient(clientid);
                            Toast.makeText(context, "You clicked edit on "+data.get(selectedItemPosition).getName(), Toast.LENGTH_SHORT).show();
                            break;
                    }

                    return false;
                }
            });
            popup.show();
        }

        private void editClient(String clientid) {

            Intent intent = new Intent(context, EditClientActivity.class);
            intent.putExtra("clientid",clientid);
            context.startActivity(intent);

        }

        private void deleteClient(final String clientid) {


            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Client");
            reference.child(clientid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    line = dataSnapshot.child("line").getValue().toString();
                    reference.child(clientid).removeValue();

                    DatabaseReference linereference = FirebaseDatabase.getInstance().getReference()
                            .child("Line");
                    linereference.child(line).child(clientid).removeValue();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }
}