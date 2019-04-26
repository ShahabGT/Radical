package ir.radical_app.radical.data;

import java.util.ArrayList;

import ir.radical_app.radical.arch.Bookmark.BookmarkResponse;
import ir.radical_app.radical.arch.Category.ShopsCategoryResponse;
import ir.radical_app.radical.arch.Search.SearchResponse;
import ir.radical_app.radical.arch.BuyHistory.BuyHistoryResponse;
import ir.radical_app.radical.arch.Upgrade.UpgradeResponse;
import ir.radical_app.radical.models.BuyDetailsModel;
import ir.radical_app.radical.models.CategoryModel;
import ir.radical_app.radical.models.ChatModel;
import ir.radical_app.radical.models.HelpModel;
import ir.radical_app.radical.models.JsonResponse;
import ir.radical_app.radical.arch.Shops.ShopsResponse;
import ir.radical_app.radical.models.ShopDetailsModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("login.php")
    Call<JsonResponse> doLogin(
            @Field("number") String number
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("auth.php")
    Call<JsonResponse> doAuth(
            @Field("number") String number,
            @Field("fbtoken") String FBToken,
            @Field("code") String code
    );
    //___________________________________________


    @FormUrlEncoded
    @POST("getprofile.php")
    Call<JsonResponse> getProfile(
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("setprofile.php")
    Call<JsonResponse> setProfile(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("name") String name,
            @Field("family") String family,
            @Field("sex") int sex,
            @Field("email") String email,
            @Field("education") int education,
            @Field("job") int job,
            @Field("region") int region,
            @Field("invitecode") String inviteCode,
            @Field("isfirst") int isfirst
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("getwallet.php")
    Call<JsonResponse> getWallet(
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________

    @GET("getshops.php")
    Call<ShopsResponse> getShops(
            @Query("start") int start,
            @Query("size") int size,
            @Query("category") String category

    );
    //___________________________________________

    @GET("getbookmarks.php")
    Call<BookmarkResponse> getBookmarks(
            @Query("start") int start,
            @Query("size") int size,
            @Query("number") String number,
            @Query("accesstoken") String accesstoken

    );
    //___________________________________________

    @GET("search.php")
    Call<SearchResponse> search(
            @Query("start") int start,
            @Query("size") int size,
            @Query("word") String word

    );
    //___________________________________________

    @GET("getshops.php")
    Call<ShopsCategoryResponse> getCategoryShops(
            @Query("start") int start,
            @Query("size") int size,
            @Query("category") String category

    );

    //___________________________________________

    @GET("getcategories.php")
    Call<CategoryModel> getCategories();
    //___________________________________________


    @FormUrlEncoded
    @POST("getshopdetails.php")
    Call<ShopDetailsModel> getShopDetails(
            @Field("shopid") String shopId,
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________


    @FormUrlEncoded
    @POST("bookmark.php")
    Call<JsonResponse> updateBookmark(
            @Field("shopid") String shopId,
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________


    @FormUrlEncoded
    @POST("rate.php")
    Call<JsonResponse> updateRate(
            @Field("shopid") String shopId,
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("rate") String rate
    );
    //___________________________________________


    @FormUrlEncoded
    @POST("resend.php")
    Call<JsonResponse> resendSMS(
            @Field("number") String number
    );
    //___________________________________________


    @FormUrlEncoded
    @POST("validation.php")
    Call<JsonResponse> validate(
            @Field("platform") String platform,
            @Field("version") int version,
            @Field("platform_version") String platformVersion,
            @Field("number") String number,
            @Field("accesstoken") String accessToken

    );
    //___________________________________________


    @GET("gethelp.php")
    Call<HelpModel> getHelp();
    //___________________________________________

    @FormUrlEncoded
    @POST("sendmessage.php")
    Call<JsonResponse> sendMessage(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("message") String message
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("getmessages.php")
    Call<ChatModel> getMessages(
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("getshopqr.php")
    Call<ShopDetailsModel> getShopQr(
            @Field("qrid") String qrId,
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("buyfromshop.php")
    Call<JsonResponse> buyFromShop(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("shop_id") String shopId,
            @Field("amount") String amount,
            @Field("discount") String discount,
            @Field("pay") String pay,
            @Field("price[]")ArrayList<String> price,
            @Field("price_discount[]")ArrayList<String> priceDiscount,
            @Field("discount_percent[]")ArrayList<String> discountPercent,
            @Field("discount_title[]")ArrayList<String> discountTitle
            );
    //___________________________________________

    @FormUrlEncoded
    @POST("getbuyhistory.php")
    Call<BuyHistoryResponse> getBuyHistory(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("start") int start,
            @Field("size") int size
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("getupgradehistory.php")
    Call<UpgradeResponse> getUpgradeHistory(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("start") int start,
            @Field("size") int size
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("getbuydetails.php")
    Call<BuyDetailsModel> getBuyDetails(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("shop_buy_id") String shopBuyId
    );
    //___________________________________________


    @GET("getplan.php")
    Call<JsonResponse> getPlan();
    //___________________________________________


    @GET("getabout.php")
    Call<JsonResponse> getAbout();
    //___________________________________________

    @FormUrlEncoded
    @POST("getinvites.php")
    Call<JsonResponse> getInvites(
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );
    //___________________________________________

    @FormUrlEncoded
    @POST("getstats.php")
    Call<ShopsResponse> getStats(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("year") String year
    );
    //___________________________________________


    @FormUrlEncoded
    @POST("checkgiftcode.php")
    Call<JsonResponse> checkGiftcode(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("giftcode") String giftcode
    );

    //___________________________________________

    @FormUrlEncoded
    @POST("initbuy.php")
    Call<JsonResponse> upgrade(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("plan") int plan,
            @Field("giftcode") String giftcode,
            @Field("wallet") int wallet,
            @Field("description") String description,
            @Field("amount") String amount
    );

    //___________________________________________

    @FormUrlEncoded
    @POST("buywallet.php")
    Call<JsonResponse> upgradeWallet(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("plan") int plan,
            @Field("wallet") int wallet,
            @Field("description") String description);

    //___________________________________________

    @FormUrlEncoded
    @POST("getgift.php")
    Call<JsonResponse> getGift(
            @Field("number") String number,
            @Field("accesstoken") String accessToken,
            @Field("count") String count);
    //___________________________________________

    @FormUrlEncoded
    @POST("getsocialgift.php")
    Call<JsonResponse> getSocialGift(
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );

    //___________________________________________

    @FormUrlEncoded
    @POST("getsocialgift2.php")
    Call<JsonResponse> getSocialGift2(
            @Field("number") String number,
            @Field("accesstoken") String accessToken
    );

}
