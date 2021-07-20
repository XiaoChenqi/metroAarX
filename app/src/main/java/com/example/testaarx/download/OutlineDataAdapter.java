package com.example.testaarx.download;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.testaarx.R;


import java.util.List;

import androidx.annotation.Nullable;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/10/24 11:10 AM
 */
public class OutlineDataAdapter extends BaseQuickAdapter<DownloadProgressEntity, BaseViewHolder> {

    public OutlineDataAdapter(@Nullable List<DownloadProgressEntity> data) {
        super(R.layout.fragment_outline_data_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownloadProgressEntity item) {
        if (item == null) {
            return;
        }

        helper.setText(R.id.name_tv, item.getName());
        helper.setProgress(R.id.progress_pb, item.getProgress(), item.getMax());
        
        switch (item.getType()) {
            case DownloadStatus.DOWNLOAD_STATUS_NOT_DOWNLOAD:
                helper.setText(R.id.status_tv, R.string.app_not_download);
                helper.setTextColor(R.id.status_tv, mContext.getResources().getColor(R.color.grey_9));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_HAVE_DOWNLOAD:
                helper.setText(R.id.status_tv, R.string.app_download);
                helper.setTextColor(R.id.status_tv, mContext.getResources().getColor(R.color.green_1ab394));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_FAILED_DOWNLOAD:
                helper.setText(R.id.status_tv, R.string.app_download_fail);
                helper.setTextColor(R.id.status_tv, mContext.getResources().getColor(R.color.red_f55858));
                break;
            case DownloadStatus.DOWNLOAD_STATUS_HAVE_UPDATE:
                helper.setText(R.id.status_tv, R.string.app_download_new);
                helper.setTextColor(R.id.status_tv, mContext.getResources().getColor(R.color.orange_ff9900));
                break;
        }
        
        helper.setVisible(R.id.progress_pb, item.getProgress() != item.getMax());
        int layoutPosition = helper.getLayoutPosition();
        helper.setGone(R.id.short_line, layoutPosition != getData().size() - 1);
        helper.setGone(R.id.long_line, layoutPosition == getData().size() - 1);
    }
}
