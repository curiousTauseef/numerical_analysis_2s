package com.sands.aplication.numeric.fragments.listViewCustomAdapter;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sands.aplication.numeric.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by User on 3/14/2017.
 */

public class NewtonListAdapter extends ArrayAdapter<Newton> {

    private final Context mContext;
    private final int mResource;
    private int lastPosition = -1;

    /**
     * Default constructor for the PersonListAdapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public NewtonListAdapter(Context context, int resource, ArrayList<Newton> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //get the persons information
        String n = Objects.requireNonNull(getItem(position)).getN();
        String xn = Objects.requireNonNull(getItem(position)).getXn();
        String fXn = Objects.requireNonNull(getItem(position)).getFXn();
        String fdXn = Objects.requireNonNull(getItem(position)).getFDXn();
        String error = Objects.requireNonNull(getItem(position)).getError();

        //Create the person object with the information
        Newton newton = new Newton(n, xn, fXn, fdXn, error);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.textViewN = convertView.findViewById(R.id.textViewN);
            holder.textViewXn = convertView.findViewById(R.id.textViewXn);
            holder.textViewFXn = convertView.findViewById(R.id.textViewFXn);
            holder.textViewFDXn = convertView.findViewById(R.id.textViewFDXn);
            holder.textViewError = convertView.findViewById(R.id.textViewError);

            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.textViewN.setText(newton.getN());
        holder.textViewXn.setText(newton.getXn());
        holder.textViewFXn.setText(newton.getFXn());
        holder.textViewFDXn.setText(newton.getFDXn());
        holder.textViewError.setText(newton.getError());

        if (holder.textViewN.getText() == "n") {
            holder.textViewN.setTextColor(Color.WHITE);
            holder.textViewXn.setTextColor(Color.WHITE);
            holder.textViewFXn.setTextColor(Color.WHITE);
            holder.textViewFDXn.setTextColor(Color.WHITE);
            holder.textViewError.setTextColor(Color.WHITE);
            holder.textViewN.setBackgroundColor(Color.rgb(63, 81, 181));
            holder.textViewXn.setBackgroundColor(Color.rgb(63, 81, 181));
            holder.textViewFXn.setBackgroundColor(Color.rgb(63, 81, 181));
            holder.textViewFDXn.setBackgroundColor(Color.rgb(63, 81, 181));
            holder.textViewError.setBackgroundColor(Color.rgb(63, 81, 181));
        } else {
            holder.textViewN.setTextColor(Color.BLACK);
            holder.textViewXn.setTextColor(Color.BLACK);
            holder.textViewFXn.setTextColor(Color.BLACK);
            holder.textViewFDXn.setTextColor(Color.BLACK);
            holder.textViewError.setTextColor(Color.BLACK);
            holder.textViewN.setBackgroundColor(Color.TRANSPARENT);
            holder.textViewXn.setBackgroundColor(Color.TRANSPARENT);
            holder.textViewFXn.setBackgroundColor(Color.TRANSPARENT);
            holder.textViewFDXn.setBackgroundColor(Color.TRANSPARENT);
            holder.textViewError.setBackgroundColor(Color.TRANSPARENT);
        }


        return convertView;
    }

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView textViewN;
        TextView textViewXn;
        TextView textViewFXn;
        TextView textViewFDXn;
        TextView textViewError;
    }
}
