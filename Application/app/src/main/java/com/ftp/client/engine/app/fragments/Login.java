package com.ftp.client.engine.app.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ftp.client.R;
import com.ftp.client.application.WebListActivity;
import com.ftp.client.engine.app.settings.Settings;
import com.ftp.client.engine.widget.MaterialEditText;

public class Login extends Fragment {
    
    public static String TAG = Login.class.getSimpleName();
    private MaterialEditText email;
    private MaterialEditText password;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        email = getView().findViewById(R.id.loginEmail);
        password = getView().findViewById(R.id.loginPassword);

        getView().findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(authenticate())
                {
                    startActivity(new Intent(getContext(), WebListActivity.class));
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(getContext(),"Wrong email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean authenticate() {
        Settings p = new Settings(getContext());

        if(p.getEmail().equals(email.getText().toString()) && p.getPassword().equals(password.getText().toString()))
        {
            p.setLoginStatus(true);
            return true;
        }

        return false;
    }
}
