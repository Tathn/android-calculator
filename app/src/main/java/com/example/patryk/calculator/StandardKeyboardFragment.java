package com.example.patryk.calculator;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class StandardKeyboardFragment extends Fragment {
    private OnButtonClickListener callback;

    interface OnButtonClickListener{
        void onButtonClick(View view);
    }

    public StandardKeyboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_standard_keyboard, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnButtonClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement StandardKeyboardFragment.OnButtonClickListener");
        }
    }

    public void onButtonClick(View view) {
        callback.onButtonClick(view);
    }
}
