
/**
 * ローディング画像をオーバーレイ表示する。
 */
function loading(overlayBox) {
  if($("#" + overlayBox + " div.overlay").length == 0) {
    var loading = "<div class='overlay'><i class='fa fa-refresh fa-spin'></i></div>";
    $("#" + overlayBox).append(loading);
  }
}

/**
 * ローディング画像を非表示にする。
 */
function removeLoading(overlayBox) {
  $("#" + overlayBox + " div.overlay").remove();
}

$(function() {
  $('.fade-out').fadeOut(3000);
});