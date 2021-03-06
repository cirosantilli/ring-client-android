/*
 *  Copyright (C) 2004-2016 Savoir-faire Linux Inc.
 *
 *  Author: Alexandre Lision <alexandre.lision@savoirfairelinux.com>
 *  Author: Adrien Béraud <adrien.beraud@savoirfairelinux.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package cx.ring.adapters;

import java.io.File;
import java.util.List;

import cx.ring.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cx.ring.model.account.Account;

public class AccountSelectionAdapter extends BaseAdapter {

    private static final String TAG = AccountSelectionAdapter.class.getSimpleName();

    private List<Account> accounts;
    private Context mContext;
    private int selectedAccount = -1;
    private static final String DEFAULT_ACCOUNT_ID = "IP2IP";

    public AccountSelectionAdapter(Context cont, List<Account> newList) {
        super();
        accounts = newList;
        mContext = cont;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Account getItem(int pos) {
        return accounts.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View rowView = convertView;
        AccountView entryView = null;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(R.layout.item_account_selected, parent, false);

            entryView = new AccountView();
            entryView.alias = (TextView) rowView.findViewById(R.id.account_alias);
            entryView.host = (TextView) rowView.findViewById(R.id.account_host);
            entryView.error = (ImageView) rowView.findViewById(R.id.error_indicator);
            rowView.setTag(entryView);
        } else {
            entryView = (AccountView) rowView.getTag();
        }
        entryView.error.setColorFilter(mContext.getResources().getColor(R.color.error_red));

/*
        entryView.alias.setText(accounts.get(pos).getAlias());

        entryView.host.setText(accounts.get(pos).getHost() + " - " + accounts.get(pos).getRegistered_state());
        // accManager.displayAccountDetails(accounts.get(pos), entryView);
        entryView.error.setVisibility(View.GONE);
*/
        updateAccountView(entryView, accounts.get(pos));

        return rowView;
    }

    @Override
    public View getDropDownView(int pos, View convertView, ViewGroup parent) {
        View rowView = convertView;
        AccountView entryView = null;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(R.layout.item_account, null);

            entryView = new AccountView();
            entryView.alias = (TextView) rowView.findViewById(R.id.account_alias);
            entryView.host = (TextView) rowView.findViewById(R.id.account_host);
            entryView.error = (ImageView) rowView.findViewById(R.id.error_indicator);
            rowView.setTag(entryView);
        } else {
            entryView = (AccountView) rowView.getTag();
        }
        entryView.error.setColorFilter(mContext.getResources().getColor(R.color.error_red));

        updateAccountView(entryView, accounts.get(pos));
        return rowView;
    }

    private void updateAccountView(AccountView entryView, Account acc) {
        entryView.alias.setText(acc.getAlias());
        if (acc.isRing()) {
            entryView.host.setText(acc.getBasicDetails().getUsername());
        } else {
            entryView.host.setText(acc.getBasicDetails().getUsername() + "@" + acc.getBasicDetails().getHostname());
        }
        entryView.error.setVisibility(acc.isRegistered() ? View.GONE : View.VISIBLE);
    }

    public Account getAccount(String accountID) {
        for(Account acc : accounts) {
            if(acc.getAccountID().contentEquals(accountID))
                return acc;
        }
        return null;
    }

    /*********************
     * ViewHolder Pattern
     *********************/
    public class AccountView {
        public TextView alias;
        public TextView host;
        public ImageView error;
    }

    public void setSelectedAccount(int pos) {
        selectedAccount = pos;
    }

    public Account getSelectedAccount() {
        if (selectedAccount == -1) {
            return null;
        }
        return accounts.get(selectedAccount);
    }

    public void removeAll() {
        accounts.clear();
        notifyDataSetChanged();

    }

    public void addAll(List<Account> results) {
        accounts.addAll(results);
        notifyDataSetChanged();
    }

    public void replaceAll(List<Account> results) {
        accounts.clear();
        accounts.addAll(results);
        notifyDataSetChanged();
    }

    /**
     * Modify state of specific account
     */
    public void updateAccount(String accoundID, String state, int code) {
        Log.i(TAG, "updateAccount ");

        for (Account a : accounts) {
            if (a.getAccountID().contentEquals(accoundID)) {
                a.setRegistrationState(state, code);
                Log.i(TAG, "updateAccount " + accoundID + " " + code);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public String getAccountOrder() {
        String result = DEFAULT_ACCOUNT_ID + File.separator;
        String selectedID = accounts.get(selectedAccount).getAccountID();
        result += selectedID + File.separator;

        for (Account a : accounts) {
            if (a.getAccountID().contentEquals(selectedID)) {
                continue;
            }
            result += a.getAccountID() + File.separator;
        }

        return result;
    }

}
