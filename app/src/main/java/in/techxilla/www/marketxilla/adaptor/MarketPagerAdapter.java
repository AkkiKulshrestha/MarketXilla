package in.techxilla.www.marketxilla.adaptor;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import in.techxilla.www.marketxilla.fragment.ActiveByValueFragment;
import in.techxilla.www.marketxilla.fragment.ActiveByVolumeFragment;
import in.techxilla.www.marketxilla.fragment.ActiveCallsIndexFragment;
import in.techxilla.www.marketxilla.fragment.ActiveCallsStockFragment;
import in.techxilla.www.marketxilla.fragment.ActiveFutureFragment;
import in.techxilla.www.marketxilla.fragment.ActivePutsIndexFragment;
import in.techxilla.www.marketxilla.fragment.ActivePutsStockFragment;
import in.techxilla.www.marketxilla.fragment.TopGainerFragment;
import in.techxilla.www.marketxilla.fragment.TopLosserFragment;

public class MarketPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    public MarketPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {



            case 0:
                TopGainerFragment top_gainer = new TopGainerFragment();
                return top_gainer;
            case 1:
                TopLosserFragment top_looser = new TopLosserFragment();
                return top_looser;
            case 2:
                ActiveCallsStockFragment activeCallsStockFragment = new ActiveCallsStockFragment();
                return activeCallsStockFragment;
            case 3:
                ActivePutsStockFragment activePutsStockFragment = new ActivePutsStockFragment();
                return activePutsStockFragment;
            case 4:
                ActiveCallsIndexFragment activeCallsIndexFragment = new ActiveCallsIndexFragment();
                return activeCallsIndexFragment;
            case 5:
                ActivePutsIndexFragment activePutsIndexFragment = new ActivePutsIndexFragment();
                return activePutsIndexFragment;
            case 6:
                ActiveByValueFragment active_by_value = new ActiveByValueFragment();
                return active_by_value;
            case 7:
                ActiveByVolumeFragment active_by_volume = new ActiveByVolumeFragment();
                return active_by_volume;
            case 8:
                ActiveFutureFragment activeFutureFragment = new ActiveFutureFragment();
                return activeFutureFragment;

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
