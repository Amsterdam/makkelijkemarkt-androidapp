/*
 * Copyright (C) 2016 Gemeente Amsterdam, Marktbureau
 */
package com.amsterdam.marktbureau.makkelijkemarkt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * @author marcolangebeeke
 */
public class DagvergunningFragmentProduct extends Fragment {

    // use classname when logging
    private static final String LOG_TAG = DagvergunningFragmentProduct.class.getSimpleName();

    // bind layout elements
    @Bind(R.id.notitie) EditText mNotitie;

    /**
     * Constructor
     */
    public DagvergunningFragmentProduct() {
    }

    /**
     * Callback interface so we can talk back to the activity
     */
    public interface Callback {
        void onProductFragmentReady();
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dagvergunning_fragment_product, container, false);

        // bind the elements to the view
        ButterKnife.bind(this, view);

        // get the producten from the sharedprefs and create the product selectors
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String producten = settings.getString(getContext().getString(R.string.sharedpreferences_key_markt_producten), null);
        if (producten != null) {

            // split comma-separated string into list with product strings
            List<String> productList = Arrays.asList(producten.split(","));
            if (productList.size() > 0) {

                String[] productKeys = getResources().getStringArray(R.array.array_product_key);
                String[] productTypes = getResources().getStringArray(R.array.array_product_type);
                String[] productTitles = getResources().getStringArray(R.array.array_product_title);

                // inflate the producten placeholder view
                LinearLayout placeholderLayout = (LinearLayout) view.findViewById(R.id.producten_placeholder);
                if (placeholderLayout != null) {
                    placeholderLayout.removeAllViews();
                    LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    // add multiple product item views to the placeholder view
                    for (int i = 0; i < productList.size(); i++) {

                        // get a resource id for the product
                        int id = Utility.getResId("product_" + productList.get(i), R.id.class);
                        if (id != -1) {

                            // get the corresponding product type based on the productlist item value
                            String productType = "";
                            for (int j = 0; j < productKeys.length; j++) {
                                if (productKeys[j].equals(productList.get(i))) {
                                    productType = productTypes[j];
                                }
                            }

                            // get the product item layout depending on the product type
                            View childLayout = null;
                            if (productType.equals("integer")) {
                                childLayout = layoutInflater.inflate(R.layout.dagvergunning_product_item_count, null);
                            } else if (productType.equals("boolean")) {
                                childLayout = layoutInflater.inflate(R.layout.dagvergunning_product_item_toggle, null);
                            }

                            if (childLayout != null) {
                                childLayout.setId(id);

                                // get the corresponding product title based on the productlist item value
                                String productTitle = "";
                                for (int j = 0; j < productKeys.length; j++) {
                                    if (productKeys[j].equals(productList.get(i))) {
                                        productTitle = productTitles[j];
                                    }
                                }

                                // set product name
                                TextView productNameText = (TextView) childLayout.findViewById(R.id.product_name);
                                productNameText.setText(productTitle);

                                // if product type is integer add click handlers to +- buttons
                                if (productType.equals("integer")) {

                                    // set onclickhandler on the - buttons to decrease the value of the product_count textview
                                    Button countDownButton = (Button) childLayout.findViewById(R.id.product_count_down);
                                    countDownButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            changeProductCountText(v, false);
                                        }
                                    });

                                    // set onclickhandler on the + buttons to increase the value of the product_count textview
                                    Button countUpButton = (Button) childLayout.findViewById(R.id.product_count_up);
                                    countUpButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            changeProductCountText(v, true);
                                        }
                                    });
                                }

                                // add view and move cursor to next product
                                placeholderLayout.addView(childLayout, i);
                            }
                        }
                    }
                }
            }
        }

        return view;
    }

    /**
     * Inform the activity that the product fragment is ready so it can be manipulated by the
     * dagvergunning fragment
     * @param savedInstanceState saved fragment state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // call the activity
        ((Callback) getActivity()).onProductFragmentReady();
    }

    /**
     * In/decrease the value of the product count textview
     * @param button the button that was clicked
     * @param up in- or decrease
     */
    private void changeProductCountText(View button, boolean up) {

        // get the parent view so we can reach the textview of this productrow only
        View clickedProductView = (View) button.getParent();
        if (clickedProductView != null) {

            // get the productcount textview
            TextView clickedProductText = (TextView) clickedProductView.findViewById(R.id.product_count);
            if (clickedProductText != null) {
                int clickedCount = Integer.valueOf(clickedProductText.getText().toString());
                if (!up && clickedCount > 0) {
                    // decrease value, if larger then 0
                    clickedProductText.setText(String.valueOf(clickedCount - 1));
                } else if (up && clickedCount < 99) {
                    // increase value, if smaller then 99
                    clickedProductText.setText(String.valueOf(clickedCount + 1));
                }
            }
        }
    }
}