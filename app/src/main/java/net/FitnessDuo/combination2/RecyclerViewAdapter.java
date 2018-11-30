package net.FitnessDuo.combination2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    //private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Long> mImageDates = new ArrayList<>();

    //private ArrayList<String> mImages = new ArrayList<>();

    private Context mContext;

    //private ArrayList<Uri> mImagesStored = new ArrayList<>();
    private ArrayList<Uri> mImageFilepaths = new ArrayList<>();

    private ArrayList<String> mWeights = new ArrayList<>();

    DatabaseHelper mDatabaseHelper;

    //private boolean mPickingToCompare;
    private int mPickingToCompare;
    //0 = no
    //1 = Before
    //2 = after

    private Activity mActivity; //Had to add this into stuff to be able to use startActivityForResult in Compare.java on the recyclerview Activity.
    public RecyclerViewAdapter(Context context, ArrayList<Long> imageDates, ArrayList<Uri> imageFilepaths, ArrayList<String> weights, int pickingToCompare, Activity mActivity) {
        //mImageNames = imageNames;
        mImageDates = imageDates;

        //mImages = images;

        mContext = context;

        //mImagesStored = imagesStored;
        mImageFilepaths = imageFilepaths;

        mWeights = weights;

        mPickingToCompare = pickingToCompare;
        this.mActivity=mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(net.FitnessDuo.combination2.R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                //.load(mImages.get(position))
                //.load(mImageFilepaths.get(position))
                .load(new File(mImageFilepaths.get(position).getPath()))
                //I don't know why the fuck that works^
                .into(holder.image);


        //holder.imageName.setText(mImageNames.get(position));
        //holder.imageName.setText(mImageDates.get(position));
        //long dateInLongForm = Long.parseLong(mImageDates.get(position));
        holder.imageDate.setText(getDate(mImageDates.get(position)));
        //holder.imageDate.setText(mImageDates.get(position));

        holder.weight.setText(mWeights.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: clicked on: " + mImageNames.get(position));
                Log.d(TAG, "onClick: clicked on: " + mImageDates.get(position));

                //Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, mImageDates.get(position), Toast.LENGTH_SHORT).show();

                //Toast.makeText(mContext, mWeights.get(position), Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, mImageFilepaths.get(position).toString(), Toast.LENGTH_SHORT).show();

                String filePathText = mImageFilepaths.get(position).toString();
                Long dateValue = mImageDates.get(position);
                String weightText = mWeights.get(position).toString();
                weightText = weightText.substring(0, weightText.length() - 4);
                //Log.d(TAG, "onClick: THE WEIGHT TEXT IS: " + weightText.substring(0, weightText.length() - 4));
                //mDatabaseHelper = new DatabaseHelper(mContext); //Added for deleting an entry from the database
                mDatabaseHelper = DatabaseHelper.getInstance(mContext);
                Cursor data = mDatabaseHelper.getItemID(filePathText);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Log.d(TAG, "onClick: The ID is: " + itemID);

                    if(mPickingToCompare==0){
                        Intent editScreenIntent = new Intent(mContext, EditEntryActivity.class);
                        editScreenIntent.putExtra("id", itemID);
                        editScreenIntent.putExtra("imagePath", filePathText);
                        editScreenIntent.putExtra("date", dateValue);
                        editScreenIntent.putExtra("weight", weightText);
                        mContext.startActivity(editScreenIntent);
                        //((Activity)mContext).finish();
                    } else if(mPickingToCompare==1){
                        Intent sendToCompareIntent = new Intent(mContext, Compare.class);
                        sendToCompareIntent.putExtra("id", itemID);
                        sendToCompareIntent.putExtra("imagePath", filePathText);
                        sendToCompareIntent.putExtra("date", dateValue);
                        sendToCompareIntent.putExtra("weight", weightText);
                        //sendToCompareIntent.putExtra("beforeOrAfter", 1);
                        mActivity.setResult(Activity.RESULT_OK, sendToCompareIntent);
                        //mContext.startActivity(sendToCompareIntent);
                        ((Activity)mContext).finish();
                    } else {
                        Intent sendToCompareIntent = new Intent(mContext, Compare.class);
                        sendToCompareIntent.putExtra("id", itemID);
                        sendToCompareIntent.putExtra("imagePath", filePathText);
                        sendToCompareIntent.putExtra("date", dateValue);
                        sendToCompareIntent.putExtra("weight", weightText);
                        //sendToCompareIntent.putExtra("beforeOrAfter", 2);
                        mActivity.setResult(Activity.RESULT_OK, sendToCompareIntent);
                        //mContext.startActivity(sendToCompareIntent);
                        ((Activity)mContext).finish();
                    }
                } else {
                    toastMessage("No ID associated with that name");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //return mImageNames.size();
        return mImageDates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        //TextView imageName;
        TextView imageDate;
        TextView weight;
        RelativeLayout parentLayout;
        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(net.FitnessDuo.combination2.R.id.image);
            imageDate = itemView.findViewById(net.FitnessDuo.combination2.R.id.image_date);
            parentLayout = itemView.findViewById(net.FitnessDuo.combination2.R.id.parent_layout);
            weight = itemView.findViewById(net.FitnessDuo.combination2.R.id.weight_text);
        }
    }

    //Custom toast
    private void toastMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    //For displaying dates
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd MMM yy", cal).toString();
        return date;
    }
}
