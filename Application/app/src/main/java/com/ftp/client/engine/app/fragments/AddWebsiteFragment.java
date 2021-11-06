package com.ftp.client.engine.app.fragments;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.ftp.client.R;
import com.ftp.client.engine.app.db.DBHelper;
import com.ftp.client.engine.app.models.WebsiteItem;
import com.ftp.client.application.WebEditActivity;
import com.ftp.client.engine.app.config.Constants;

public class AddWebsiteFragment extends Fragment implements View.OnClickListener {

    private WebEditActivity mActivity;
    private Context mContext;
    private EditText mConnectionName;
    private EditText mUserName;
    private EditText mServerIP;
    private EditText mServerPort;
    private EditText mServerPassword;
    private EditText mDestination;
    private EditText mWebAddress;
    private Button mBtnAdd;


    private String connectionName;
    private String username;
    private String serverIP;
    private String serverPort;
    private String serverPassword;
    private String destination;
    private String siteAddress;

    int count = 0;
    private DBHelper mDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        mActivity = (WebEditActivity)getActivity();
        mDataBase = new DBHelper(mContext);
        mConnectionName = (EditText)view.findViewById(R.id.web_name);
        mConnectionName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // This space intentionally left blank
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    connectionName = s.toString();
                    connectionName = connectionName.replaceAll(System.getProperty("line.separator"), "");
                }
                @Override
                public void afterTextChanged(Editable s) {
                    // This one too
                }
            });
        mUserName = (EditText)view.findViewById(R.id.user);
        mServerIP = (EditText)view.findViewById(R.id.server_ip);      
        mServerPort = (EditText)view.findViewById(R.id.server_port);   
        mServerPassword = (EditText)view.findViewById(R.id.server_password);       
        mDestination = (EditText)view.findViewById(R.id.server_destination);      
        mWebAddress = (EditText)view.findViewById(R.id.web_address);
     
        

        mBtnAdd = (Button)view.findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add:
                 connectionName = mConnectionName.getText().toString();
                 username = mUserName.getText().toString(); 
                 username = username.replaceAll(System.getProperty("line.separator"),"");
                 serverIP = mServerIP.getText().toString();
                 serverPort = mServerPort.getText().toString();
                 serverPassword = mServerPassword.getText().toString();
                 destination = mDestination.getText().toString();
                 siteAddress = mWebAddress.getText().toString(); 
                 validate();
        }
    }

    private void validate() {
        boolean valid=true;
        boolean validBefore=true;

        //check connection name validity
        //p{L} matches any kind of letter from any language.
        if (connectionName.isEmpty() || (!connectionName.isEmpty() && !connectionName.matches("^[\\p{L} .'-{0-9}]+$"))) {
            mConnectionName.setError("valid characters include (a-z)(0-9)-_'.()");
            valid = false;
        } else if (connectionName.length() > 25) {
            mConnectionName.setError("connection name can only be 25 chars");
            valid = false;
        } else
            mConnectionName.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid connection name", Toast.LENGTH_SHORT).show();
            //mConnectionName.requestFocus();
        }

        //check server ip validity
        String validIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        String validHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

        if (serverIP.isEmpty() || (!serverIP.isEmpty() && (!serverIP.matches(validIpAddressRegex) && !serverIP.matches(validHostnameRegex)))) {
            mServerIP.setError("only standard formats of ip or hostname are valid");
            valid = false;
        } else
            mServerIP.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid ip or hostname", Toast.LENGTH_SHORT).show();
            //mServerIP.requestFocus();
        }

        //check server port validity
        String validPortRegex = "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";


        if (serverPort.isEmpty() || (!serverPort.isEmpty() && !serverPort.matches(validPortRegex))) {
            mServerPort.setError("valid port range is 0-65535");
            valid = false;
        } else
            mServerPort.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid port", Toast.LENGTH_SHORT).show();
            //mServerPort.requestFocus();
        }

        //check server username validity
        //string can contain only ASCII letters and digits, with hyphens, underscores and spaces as internal separators.
        // the first and last character are not separators, and that there's never more than one separator in a row
        if (username.isEmpty() || (!username.isEmpty() && !username.matches("^[A-Za-z0-9]+(?:[ ._-][A-Za-z0-9]+)*$"))) {
            mUserName.setError("valid characters include (a-z)(0-9) .-_");
            valid = false;
        } else if (username.length() > 25) {
            mUserName.setError("server username can only be 25 chars");
            valid = false;
        } else
            mUserName.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid server username", Toast.LENGTH_SHORT).show();
            //mServerUsername.requestFocus();
        }

        //check password validity
        if (!serverPassword.isEmpty() && (serverPassword.length() > 50 || !serverPassword.matches("^\\p{ASCII}+$"))) {
            mServerPassword.setError("password can only be 50 ASCII chars");
            valid = false;
        } else
            mServerPassword.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid password", Toast.LENGTH_SHORT).show();
            //mServerPassword.requestFocus();
        }

        //check destination validity
        //String validUnixPathRegex = "\([^\0 !$`&*()+]\|\\\(\ |\!|\$|\`|\&|\*|\(|\)|\+\)\)\+";
        String validUnixPathRegex = "^\\/$|(^(?=\\/)|^\\.|^\\.\\.)(\\/(?=[^/\0])[^/\0]+)*\\/?$";
        //String validWindowsPathRegex ="([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";

        if (!destination.isEmpty() && !destination.matches(validUnixPathRegex)) {
            mDestination.setError("only unix paths are valid");
            valid = false;
        } else if (destination.length() > 200) {
            mDestination.setError("path can only be 200 chars");
            valid = false;
        } else
            mDestination.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid destination", Toast.LENGTH_SHORT).show();
            //mDestination.requestFocus();
        }

        if (siteAddress.isEmpty() || (!siteAddress.isEmpty() && !siteAddress.matches("^[\\p{L} .'-{0-9}]+$"))) {
            mWebAddress.setError("valid characters include (a-z)(0-9)-_'.()");
            valid = false;
        } else if (siteAddress.length() > 200) {
            mWebAddress.setError("site address can only be 200 chars");
            valid = false;
        } else if(siteAddress.startsWith("http://")){
            mWebAddress.setError("site address can only be wwww");
            valid = false;
        } else if(siteAddress.startsWith("https://")){
            mWebAddress.setError("site address can only be wwww");
            valid = false;
        }
         else
            mWebAddress.setError(null);

        if (!valid && validBefore) {
            validBefore = false;
            Toast.makeText(getActivity(), "enter a valid site address", Toast.LENGTH_SHORT).show();
            //mConnectionName.requestFocus();
        }

        if (valid) {
            try {
                if (siteAddress.startsWith("www.")) {
                    siteAddress = Constants.HTTP + siteAddress;
                    
                } else if (siteAddress.startsWith("ftp.")) {
                    siteAddress = Constants.FTP + siteAddress;
                    
                } else if (siteAddress.startsWith(Constants.HTTP) || siteAddress.startsWith(Constants.HTTPS)) {
                    siteAddress = Constants.HTTP + siteAddress;
                    
                } else if (siteAddress.startsWith(Constants.FILE)) {
                    siteAddress = Constants.FILE + siteAddress;
                    
                }
                mDataBase.addRecording(connectionName, username, serverIP, serverPort, serverPassword, siteAddress);
                
        } catch (Exception e) {
                e.printStackTrace();
            }
            mActivity.finish();
            showToast("Add Website Success..!");
        }

    }

    public void showToast(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
