package $packagename;

import com.sskj.common.base.BaseFragment;

import  $rPackageName.R;
import android.os.Bundle;

/**
 * @author $author
 * Create at  $date
 */
public class $nameFragment extends BaseFragment<$namePresenter> {


    @Override
    public int getLayoutId() {
        return R.layout.$layout;
    }

    @Override
    public $namePresenter getPresenter() {
        return new $namePresenter();
    }

    @Override
    public void initView() {

    }


    @Override
    public void initData() {

    }

    @Override
    public void loadData() {

    }

   public static $nameFragment newInstance() {
        $nameFragment fragment = new $nameFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }



}
