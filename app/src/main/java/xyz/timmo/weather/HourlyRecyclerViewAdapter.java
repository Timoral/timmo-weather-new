package xyz.timmo.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HourlyRecyclerViewAdapter extends RecyclerView.Adapter<HourlyRecyclerViewAdapter.ViewHolder> {

    private Context context;

    private ArrayList<Integer> arrayListDT;
    private ArrayList<String> arrayListCond, arrayListTemp, arrayListWind;

    public HourlyRecyclerViewAdapter(Context c,
                                     ArrayList<Integer> alDT,
                                     ArrayList<String> alCond,
                                     ArrayList<String> alTemp,
                                     ArrayList<String> alWind) {
        context = c;
        arrayListDT = alDT;
        arrayListCond = alCond;
        arrayListTemp = alTemp;
        arrayListWind = alWind;
    }

    @Override
    public HourlyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tile, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewDT.setText(arrayListDT.get(position));
        holder.textViewCond.setText(arrayListCond.get(position));
        holder.textViewTemp.setText(arrayListTemp.get(position));
        holder.textViewWind.setText(arrayListWind.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListDT.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDT, textViewCond, textViewTemp, textViewWind;

        public ViewHolder(View view) {
            super(view);
            textViewDT = (TextView) view.findViewById(R.id.textViewDT);
            textViewCond = (TextView) view.findViewById(R.id.textViewCond);
            textViewTemp = (TextView) view.findViewById(R.id.textViewTemp);
            textViewWind = (TextView) view.findViewById(R.id.textViewWind);
        }
    }
}