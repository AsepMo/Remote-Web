package com.ftp.client.engine.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.ftp.client.R;
import com.ftp.client.engine.app.models.WebsiteItem;
import com.ftp.client.engine.app.db.DBHelper;
import com.ftp.client.engine.widget.MarqueeTextView;
import com.ftp.client.application.RemoteWebActivity;

public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder> implements DBHelper.OnDatabaseChangedListener {

    private static final String TAG = WebsiteAdapter.class.getSimpleName();

    private DBHelper mDatabase;

    WebsiteItem item;
    Context mContext;
    LinearLayoutManager llm;

    public WebsiteAdapter(Context context, LinearLayoutManager linearLayoutManager) {
        super();
        mContext = context;
        mDatabase = new DBHelper(mContext);
        mDatabase.setOnDatabaseChangedListener(this);
        llm = linearLayoutManager;
    }

    @Override
    public void onBindViewHolder(final WebsiteViewHolder holder, int position) {

        item = getItem(position);

        holder.vName.setText(item.getWebsite());
        holder.vUser.setText(item.getUserName());
        holder.vUser.startScroll();
        holder.vHost.setText(item.getServerIP());
        holder.vHost.startScroll();
      //  holder.vAt.startScroll();
        holder.vAddress.setText(item.getSitus());
        
        // define an on click listener to open PlaybackFragment
        holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item = getItem(holder.getPosition());
                    Intent mApplication = new Intent(mContext, RemoteWebActivity.class);
                    mApplication.putExtra(RemoteWebActivity.EXTRA_WEBSITE, item.getWebsite());
                    mApplication.putExtra(RemoteWebActivity.EXTRA_USER, item.getUserName());
                    mApplication.putExtra(RemoteWebActivity.EXTRA_HOST, item.getServerIP());
                    mApplication.putExtra(RemoteWebActivity.EXTRA_PORT, item.getServerPort());                 
                    mApplication.putExtra(RemoteWebActivity.EXTRA_PASSWORD, item.getServerPassword());          
                    mApplication.putExtra(RemoteWebActivity.EXTRA_SITE_ADDRESS, item.getSitus());                 
                    mContext.startActivity(mApplication);
                }
            });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ArrayList<String> entrys = new ArrayList<String>();
                    entrys.add(mContext.getString(R.string.action_open_web));
                    entrys.add(mContext.getString(R.string.action_rename_file));
                    entrys.add(mContext.getString(R.string.action_delete_file));

                    final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);


                    // Delete confirm
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.dialog_confirm_request));
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    //shareFileDialog(holder.getPosition());
                                } if (item == 1) {
                                    //renameFileDialog(holder.getPosition());
                                } else if (item == 2) {
                                    deleteFileDialog(holder.getPosition());
                                }
                            }
                        });
                    builder.setCancelable(true);
                    builder.setNegativeButton(mContext.getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                    AlertDialog alert = builder.create();
                    alert.show();

                    return false;
                }
            });
    }

    @Override
    public WebsiteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
            from(parent.getContext()).
            inflate(R.layout.layout_website_item, parent, false);

        mContext = parent.getContext();

        return new WebsiteViewHolder(itemView);
    }

    public static class WebsiteViewHolder extends RecyclerView.ViewHolder {
        public ImageView vIcon;
        public TextView vName;
        public MarqueeTextView vUser;
        public MarqueeTextView vAt;
        public MarqueeTextView vHost;
        public TextView vAddress;
        
        protected View cardView;

        public WebsiteViewHolder(View v) {
            super(v);
            vIcon = (ImageView) v.findViewById(R.id.app_icon);       
            vName = (TextView) v.findViewById(R.id.connection_name);
            vUser = (MarqueeTextView) v.findViewById(R.id.connection_user);
            vAt = (MarqueeTextView) v.findViewById(R.id.connection_at);        
            vHost = (MarqueeTextView) v.findViewById(R.id.connection_host);
            vAddress = (TextView) v.findViewById(R.id.situs_address);
            cardView = v.findViewById(R.id.cardview);
        }
    }

    @Override
    public int getItemCount() {
        return mDatabase.getCount();
    }

    public WebsiteItem getItem(int position) {
        return mDatabase.getItemAt(position);
    }

    @Override
    public void onNewDatabaseEntryAdded() {
        //item added to top of the list
        notifyItemInserted(getItemCount() - 1);
        llm.scrollToPosition(getItemCount() - 1);
    }

    @Override
    //TODO
    public void onDatabaseEntryRenamed() {

    }

    public void deleteFileDialog(final int position) {
        // File delete confirm
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(mContext);
        confirmDelete.setTitle(mContext.getString(R.string.dialog_title_delete));
        confirmDelete.setMessage(mContext.getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(mContext.getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        //remove item from database, recyclerview, and storage
                        //remove(position);
                        mDatabase.removeItemWithId(getItem(position).getId());
                        notifyItemRemoved(position);
                    } catch (Exception e) {
                        Log.e(TAG, "exception", e);
                    }

                    dialog.cancel();
                }
            });
        confirmDelete.setNegativeButton(mContext.getString(android.R.string.no),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }

}
