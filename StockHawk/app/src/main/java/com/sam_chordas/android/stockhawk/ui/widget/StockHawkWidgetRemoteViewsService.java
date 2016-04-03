package com.sam_chordas.android.stockhawk.ui.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.StockDataSet;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockHawkWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = StockHawkWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] STOCK_QUOTES_COLUMNS = {
        "DISTINCT " + QuoteColumns.SYMBOL,
        QuoteColumns.BIDPRICE,
        QuoteColumns.PERCENT_CHANGE,
        QuoteColumns.CHANGE,
        QuoteColumns.ISUP,
        QuoteColumns.OPEN,
        QuoteColumns.DAYSLOW,
        QuoteColumns.DAYSHIGH,
        QuoteColumns.PREVIOUSCLOSE
    };

    static final int INDEX_STOCK_SYMBOL = 0;
    static final int INDEX_STOCK_BIDPRICE = 1;
    static final int INDEX_STOCK_PERCENT_CHANGE = 2;
    static final int INDEX_STOCK_CHANGE = 3;
    static final int INDEX_STOCK_ISUP = 4;
    static final int INDEX_STOCK_OPEN = 5;
    static final int INDEX_STOCK_DAYSLOW = 6;
    static final int INDEX_STOCK_DAYSHIGH = 7;
    static final int INDEX_STOCK_PREVIOUS_CLOSE = 8;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        STOCK_QUOTES_COLUMNS,
                        null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item_quote);
                views.setTextViewText(R.id.widget_stock_symbol, data.getString(INDEX_STOCK_SYMBOL));
                views.setTextViewText(R.id.widget_bid_price, data.getString(INDEX_STOCK_BIDPRICE));

                if (data.getInt(INDEX_STOCK_ISUP) == 1) {
                    views.setTextColor(R.id.widget_change, getResources().getColor(R.color.material_green_700));
                } else {
                    views.setTextColor(R.id.widget_change, getResources().getColor(R.color.material_red_700));
                }

                if (Utils.showPercent){
                    views.setTextViewText(R.id.widget_change, data.getString(INDEX_STOCK_PERCENT_CHANGE));
                } else {
                    views.setTextViewText(R.id.widget_change, data.getString(INDEX_STOCK_CHANGE));
                }

                StockDataSet stockDataSet = new StockDataSet();
                stockDataSet.setSymbol(data.getString(INDEX_STOCK_SYMBOL));
                stockDataSet.setPreviousClosePrice(data.getString(INDEX_STOCK_PREVIOUS_CLOSE));
                stockDataSet.setOpenPrice(data.getString(INDEX_STOCK_OPEN));
                stockDataSet.setBidPrice(data.getString(INDEX_STOCK_BIDPRICE));
                stockDataSet.setDaysLowPrice(data.getString(INDEX_STOCK_DAYSLOW));
                stockDataSet.setDaysHighPrice(data.getString(INDEX_STOCK_DAYSHIGH));

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MyStocksActivity.STOCK_DATA_SET, stockDataSet);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
