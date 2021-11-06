package com.ftp.client.engine.app.adapters;

import android.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.File;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.ftp.client.R;
import com.ftp.client.engine.app.models.UploadItem;
import com.ftp.client.engine.app.listeners.ItemTouchHelperClass;
import com.ftp.client.engine.app.preview.IconPreview;
import com.ftp.client.engine.app.preview.MimeTypes;
import com.ftp.client.engine.app.utils.Utils;
import android.support.design.widget.Snackbar;
import java.util.Collections;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.viewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter {

    private static final String TAG = UploadAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<UploadItem> urlList;
    private UploadItem mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    private View rootView;
    public UploadAdapter(Context context, View rootView, ArrayList<UploadItem> urlList) {
        this.mContext = context;
        this.rootView = rootView;
        this.urlList = urlList; 
    }

    @Override
    public UploadAdapter.viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_upload_item, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UploadAdapter.viewHolder holder, final int position) {
        final UploadItem items = urlList.get(position);   
        final File file = new File(items.getFileThumbnail());
        // get icon
        IconPreview.getFileIcon(file, holder.fileTumbnail);
        holder.fileName.setText(items.getFileName());
        holder.fileSize.setText(items.getFileSize());
        holder.fileLastModified.setText(items.getFileLastModified());
        holder.videoDuration.setText(items.getVideoDuration());
        final String ext = FilenameUtils.getExtension(items.getFilePath());
        if (Arrays.asList(MimeTypes.MIME_VIDEO).contains(ext)) {
            holder.VideoDurationLayout.setVisibility(View.VISIBLE);
        } else {
            holder.VideoDurationLayout.setVisibility(View.GONE);
        }
        
    }

    @Override
    public int getItemCount() {
        if (urlList != null) {
            return urlList.size();
        }
        return 0;
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView fileTumbnail;
        TextView fileName;
        TextView fileSize;
        TextView fileLastModified;
        TextView videoDuration;
        LinearLayout VideoDurationLayout;

        public viewHolder(View itemView) {
            super(itemView);
            fileTumbnail = (ImageView) itemView.findViewById(R.id.file_thumbnail);          
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileSize = (TextView) itemView.findViewById(R.id.file_size);  
            fileLastModified = (TextView) itemView.findViewById(R.id.file_size);  

            videoDuration = (TextView) itemView.findViewById(R.id.video_duration);  
            VideoDurationLayout = (LinearLayout) itemView.findViewById(R.id.duration_layout);  

        }

    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(urlList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(urlList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemRemoved(final int position) {
        
        mJustDeletedToDoItem = urlList.remove(position);
        mIndexOfDeletedToDoItem = position;
        notifyItemRemoved(position);
        String toShow = "Upload Item";
        Snackbar.make(rootView, "Deleted " + toShow, Snackbar.LENGTH_LONG)
            .setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    urlList.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                   
                    notifyItemInserted(mIndexOfDeletedToDoItem);
                }
            }).show();
    }
    
    public void addFiles(ArrayList<UploadItem> uploadList, String fileUpload) {    
        File file = new File(fileUpload);

        UploadItem item = new UploadItem();
        item.setFileName(file.getName());
        item.setFileThumbnail(file.getAbsolutePath());
        item.setFilePath(file.getAbsolutePath());
        item.setFileSize(FileUtils.byteCountToDisplaySize(file.length()));
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
        String date = format.format(file.lastModified());
        final String type = FilenameUtils.getExtension(fileUpload); 
        if (Arrays.asList(MimeTypes.MIME_VIDEO).contains(type)) {  
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getAbsolutePath());
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            item.setVideoDuration(Utils.timeConversion(Long.parseLong(duration)));                     
        } else {
            item.setVideoDuration("0");                   
        }
        item.setFileLastModified(date);
        uploadList.add(item);
        notifyDataSetChanged();
    }
}
