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

public class LeaderboardAdapter extends ArrayAdapter<LeaderboardDataUnit> {


    private ArrayList<LeaderboardDataUnit> dataSet;

    private Context mContext;

    public LeaderboardAdapter(ArrayList<LeaderboardDataUnit> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
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

    private class ViewHolder{
        TextView holderRank;
        ImageView holderImage;
        TextView holderName;
        TextView holderHours;
        boolean isSet;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            System.out.println("Täälä");
        }
    }
}
