package com.kyledennis.stockquote;

import java.util.Arrays;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	public final static String STOCK_SYMBOL = "com.example.stockquote.STOCK";
	
	private SharedPreferences stockSymbolsEntered;
	
	private TableLayout stockTableScrollView;
	
	private EditText stockSymbolEditText;
	
	Button enterStockSymbolButton;
	Button deleteStocksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);      
        
        stockSymbolsEntered = getSharedPreferences("stockList", MODE_PRIVATE);
        
        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);
        
        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);
        
        enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);
        
        deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);
        
        enterStockSymbolButton.setOnClickListener(enterStockButtonListener);
        deleteStocksButton.setOnClickListener(deleteStocksButtonListener);
        
        updateSavedStockList(null);
        
    }
    
    private void updateSavedStockList(String newStockSymbol) {
    	String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
    	
    	Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);
    	
    	if(newStockSymbol != null){
    		insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));
    	} else {
    		for(int i = 0; i < stocks.length; i++)
    		{
    			insertStockInScrollView(stocks[i], i);
    		}
    	}
    }
    
    private void saveStockSymbol(String newStock) {
    	String isTheStockNew = stockSymbolsEntered.getString(newStock, null);
    	
    	SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
    	
    	preferencesEditor.putString(newStock, newStock);
    	preferencesEditor.apply();
    	
    	if(isTheStockNew == null){
    		updateSavedStockList(newStock);
    	}
    }
    
    private void insertStockInScrollView(String stock, int arrayIndex){
    	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);
    	
    	TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);
    	
    	newStockTextView.setText(stock);
    	
    	Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);
    	
    	stockQuoteButton.onClickListener(getStockActivityListener);
    	
    	Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);
    	
    	quoteFromWebButton.setOnClickListener(getStockFromWebSiteListener);
    	
    	stockTableScrollView.addView(newStockRow, arrayIndex);
    }
    
    public OnClickListener enterStockButtonListener = new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if(stockSymbolEditText.getText().length() > 0){
				saveStockSymbol(stockSymbolEditText.getText().toString());
				
				stockSymbolEditText.setText("");
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				
				imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
			}else {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				
				builder.setTitle(R.string.invalid_stock_symbol);
				
				builder.setPositiveButton(R.string.ok, null);
				
				builder.setMessage(R.string.missing_stock_symbol);
				
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
				
			}
		}
    	
    };
    
    private void deleteAllStocks(){
    	stockTableScrollView.removeAllViews();
    }

    public OnClickListener deleteStocksButtonListener = new OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			deleteAllStocks();
			
			SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();
			
			preferencesEditor.clear();
			preferencesEditor.apply();
		}
    	
    };
    
    public OnClickListener getStockActivityListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			TableRow tableRow = (TableRow) v.getParent();
			
			TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);
			
			String stockSymbol = stockTextView.getText().toString();
			
			Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
			
			intent.putExtra(STOCK_SYMBOL, stockSymbol);
			
			startActivity(intent);
		}
    	
    };
    
    public OnClickListener getStockFromWebsiteListener = new OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			TableRow tableRow = (TableRow) v.getParent();
			
			TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);
			
			String stockSymbol = stockTextView.getText().toString();
			
			String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;
			
			Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
			
			startActivity(getStockWebPage);
		}
    	
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    

}
