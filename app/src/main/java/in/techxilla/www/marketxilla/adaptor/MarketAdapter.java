/*
package in.techxilla.www.marketxilla.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.fragment.HomeFragment;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.model.MarketModel;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter> {

    private ArrayList<MarketModel> marketArrayList;
    Context mCtx;

    public  MarketAdapter(Context mCtx, ArrayList<MarketModel> marketArrayList) {
        this.mCtx = mCtx;
        this.marketArrayList = marketArrayList;

    }

    @NonNull
    @Override
    public MarketAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_market_info, viewGroup, false);
        return new (view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketAdapter holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }
}
*/
