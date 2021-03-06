package samih.tiko.tamk.fi.codecount.leaderboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import samih.tiko.tamk.fi.codecount.R;

/**
 * Adapter for Leaderboards ListView
 */
public class LeaderboardAdapter extends ArrayAdapter<LeaderboardDataUnit> {


    /**
     * List of data needed for updating ListView
     */
    private ArrayList<LeaderboardDataUnit> dataSet;

    /**
     * Context
     */
    private Context mContext;

    /**
     * Constructor that inits data
     * @param data Array of LeaderboardDataUnits
     * @param context context
     */
    public LeaderboardAdapter(ArrayList<LeaderboardDataUnit> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    /**
     * Sets single View in list
     */
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;


        if (view == null) {
            //Only creates new view when recycling isn't possible

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.holderRank = (TextView) view.findViewById(R.id.rank);
            holder.holderImage = (ImageView) view.findViewById(R.id.profile_pic);
            holder.holderName = (TextView) view.findViewById(R.id.username);
            holder.holderHours = (TextView) view.findViewById(R.id.coding_time);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        LeaderboardDataUnit currentUnit = dataSet.get(position);

        holder.holderRank.setText(currentUnit.getRank());

        holder.holderImage.setImageBitmap(LeaderboardActivity.profilePics[position]);

        holder.holderName.setText(currentUnit.getName());

        holder.holderHours.setText(currentUnit.getCodingtime());

        return view;
    }

    /**
     * Helper class that holds information of single item of ListView
     */
    private class ViewHolder{
        TextView holderRank;
        ImageView holderImage;
        TextView holderName;
        TextView holderHours;
    }
}
