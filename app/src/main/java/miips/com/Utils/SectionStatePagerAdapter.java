package miips.com.Utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mfragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentsNumber = new HashMap<>();
    private final HashMap<Integer, String> mFragmentsNames = new HashMap<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mfragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName) {
        mfragmentList.add(fragment);
        mFragments.put(fragment, mfragmentList.size() - 1);
        mFragmentsNumber.put(fragmentName, mfragmentList.size() - 1);
        mFragmentsNames.put(mfragmentList.size() - 1, fragmentName);
    }


    /**
     * Return the fragment with the name @param
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName) {
        if (mFragmentsNumber.containsKey(fragmentName)) {
            return mFragmentsNumber.get(fragmentName);
        }else{
            return null;
        }
    }


    /**
     *Return the fragment with the name @fragment
     * @param fragment
     * @return
     */
    public Integer getFragmentNumber(Fragment fragment) {
        if (mFragmentsNumber.containsKey(fragment)) {
            return mFragmentsNumber.get(fragment);
        }else{
            return null;
        }
    }

    /**
     *Return the fragment with the name @fragmentNumber
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber) {
        if (mFragmentsNames.containsKey(fragmentNumber)) {
            return mFragmentsNames.get(fragmentNumber);
        }else{
            return null;
        }
    }


}
