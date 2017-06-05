package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gaoyunfan.cardweather.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tool.SPUtil;

/**
 * Created by Administrator on 2017/6/4.
 */

public class CityCollectorAdapter extends RecyclerView.Adapter<CityCollectorAdapter.MyHolder> implements View.OnClickListener
{
    private Context context;
    private List<String> preBeanList;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener = null;

    @Override
    public void onClick(View v)
    {
        if (mOnItemClickListener != null)
        {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    //define interface
    public interface OnItemClickListener
    {
        void onItemClick(View view, String name);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }

    public CityCollectorAdapter(Context context, List<String> preBeanList)
    {
        this.context = context;
        this.preBeanList = preBeanList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CityCollectorAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = layoutInflater.inflate(R.layout.city_collect_item, parent, false);
        view.setOnClickListener(this);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(CityCollectorAdapter.MyHolder holder, int position)
    {
        holder.cityName.setText(preBeanList.get(position));
        holder.itemView.setTag(preBeanList.get(position));
    }

    public void deleteItem(String name)
    {
        int position = preBeanList.indexOf(name);
        preBeanList.remove(position);
        notifyItemRemoved(position);
        SPUtil.DeleteStarCity(context, name);
    }

    public void deleteItem(int position)
    {
        String name = preBeanList.get(position);
        preBeanList.remove(position);
        notifyItemRemoved(position);
        SPUtil.DeleteStarCity(context, name);
    }

    @Override
    public int getItemCount()
    {
        return preBeanList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.city_name)
        TextView cityName;

        public MyHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
