package adapter;

import android.content.Context;
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

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = layoutInflater.inflate(R.layout.recy_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        PredictBean predictBean = preBeanList.get(position);
        /**
         * 此处加一个if判断该放什么图标
         */
        holder.desPic.setImageResource(picArray[0]);
        holder.date.setText(predictBean.getDate());
        holder.time.setText(predictBean.getWeekDay());
        holder.temp.setText(predictBean.getTemp());
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


        public MyHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
