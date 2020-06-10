package com.rabbitt.momobill.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.fragment.InvoiceFrag;
import com.rabbitt.momobill.model.ProductInvoice;

import java.util.ArrayList;
import java.util.List;

public class CartSheet extends BottomSheetDialogFragment implements CartAdapter.OnRecyleItemListener {

    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data = new ArrayList<>();
    private CartAdapter productAdapter;
    private InvoiceFrag invoiceFrag;

    public CartSheet(List<ProductInvoice> data, InvoiceFrag invoiceFrag, InvoiceFrag frag) {
        this.data = data;
        this.invoiceFrag = invoiceFrag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.cart_bottom_sheet, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        invoice_recycler = v.findViewById(R.id.recycler_cart);

    }

    private void updateRecycler(List<ProductInvoice> data) {
        productAdapter = new CartAdapter(data, invoiceFrag, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        invoice_recycler.setLayoutManager(gridLayoutManager);
        invoice_recycler.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));
        invoice_recycler.setItemAnimator(new DefaultItemAnimator());
        invoice_recycler.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
        invoice_recycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnItemClick(int position) {

    }
}
