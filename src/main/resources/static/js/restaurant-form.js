/* 맛집 등록 폼: 주소로 위도/경도 자동 입력 (Kakao 주소 검색) */
(function () {
  var btn = document.getElementById('geocode-btn');
  if (!btn) return;
  var msg = document.getElementById('geocode-msg');

  if (typeof kakao === 'undefined' || !kakao.maps) {
    btn.disabled = true;
    return;
  }

  kakao.maps.load(function () {
    var geocoder = new kakao.maps.services.Geocoder();
    btn.addEventListener('click', function () {
      var address = document.getElementById('address').value.trim();
      if (!address) { msg.textContent = '주소를 먼저 입력해 주세요.'; return; }
      geocoder.addressSearch(address, function (result, status) {
        if (status === kakao.maps.services.Status.OK) {
          document.getElementById('latitude').value = result[0].y;
          document.getElementById('longitude').value = result[0].x;
          msg.textContent = '좌표를 입력했습니다.';
        } else {
          msg.textContent = '좌표를 찾지 못했습니다. 직접 입력해 주세요.';
        }
      });
    });
  });
})();
