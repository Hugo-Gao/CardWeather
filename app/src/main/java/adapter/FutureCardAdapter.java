package adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaoyunfan.cardweather.R;

import java.util.ArrayList;
import java.util.List;

import Bean.PredictBean;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/18.
 */

public class FutureCardAdapter extends RecyclerView.Adapter<FutureCardAdapter.MyHolder>
{
    private Context context;
    private List<PredictBean> preBeanList;
    private LayoutInflater layoutInflater;
    private int[] picArray = new int[]{R.mipmap.sunny_logo, R.mipmap.rainy_logo,
            R.mipmap.cloudy_logo, R.mipmap.snowy_logo};
    private int[] decorPicArray = new int[]{R.mipmap.place1, R.mipmap.place2,
            R.mipmap.place3, R.mipmap.place4, R.mipmap.place5, R.mipmap.place6};

    public FutureCardAdapter(Context context,List<PredictBean> list)
    {
        preBeanList = new ArrayList<>();
        this.context = context;
        for (PredictBean bean : list)//防止传引用
        {

            preBeanList.add(bean);
        }
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void addItem(PredictBean bean)
    {
        preBeanList.add(bean);
        notifyItemInserted(preBeanList.size());
    }

    public void removeAll()
    {
        int size = preBeanList.size();
        preBeanList.clear();
        notifyItemRangeRemoved(0, size);

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = layoutInflater.inflate(R.layout.recy_item_new, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        PredictBean bean = preBeanList.get(position);
        /**
         * 此处加一个if判断该放什么图标
         */
        holder.desPic.setImageResource(picArray[0]);
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorSunny));
        if (bean.getDescribe().contains("多云") || bean.getDescribe().contains("阴天"))
        {
            holder.desPic.setImageResource(picArray[2]);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorCloudy));
        } else if (bean.getDescribe().contains("雨"))
        {
            holder.desPic.setImageResource(picArray[1]);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorRainy));
        } else if (bean.getDescribe().contains("雪"))
        {
            holder.desPic.setImageResource(picArray[3]);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorSnowy));
            holder.date.setTextColor(Color.BLACK);
            holder.time.setTextColor(Color.BLACK);
            holder.temp.setTextColor(Color.BLACK);
        }
        holder.date.setText(bean.getDate());
        holder.time.setText(bean.getWeekDay());
        holder.temp.setText(bean.getTemp());
        holder.decorPic.setImageResource(decorPicArray[position%decorPicArray.length]);
    }

    @Override
    public int getItemCount()
    {
        return preBeanList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.future_pic)
        ImageView desPic;
        @BindView(R.id.future_date_txt)
        TextView date;
        @BindView(R.id.future_temp_txt)
        TextView temp;
        @BindView(R.id.future_time_txt)
        TextView time;
        @BindView(R.id.future_card)
        CardView cardView;
        @BindView(R.id.decor_pic)
        ImageView decorPic;

        public MyHolder(View itemView)
        {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
