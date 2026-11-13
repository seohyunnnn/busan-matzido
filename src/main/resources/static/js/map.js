/* 지도 표시: 페이지 안의 [data-marker] 요소를 읽어 Kakao 지도에 마커를 찍습니다. */
(function () {
  var el = document.getElementById('map');
  if (!el) return;

  if (typeof kakao === 'undefined' || !kakao.maps) {
    el.classList.add('map-disabled');
    el.textContent = '지도를 표시하려면 Kakao 지도 JavaScript 키 설정이 필요합니다. (README 참고)';
    return;
  }

  kakao.maps.load(function () {
    var items = Array.prototype.slice.call(document.querySelectorAll('[data-marker]'));
    var map = new kakao.maps.Map(el, { center: new kakao.maps.LatLng(35.1796, 129.0756), level: 8 });
    var bounds = new kakao.maps.LatLngBounds();
    var openInfo = null;
    var entries = [];

    function buildInfo(it) {
      var box = document.createElement('div');
      box.className = 'info-window';
      var a = document.createElement('a');
      a.href = it.dataset.url;
      a.textContent = it.dataset.name;
      var p = document.createElement('p');
      p.textContent = it.dataset.summary || '';
      box.appendChild(a);
      box.appendChild(p);
      return box;
    }

    function show(entry) {
      if (openInfo) openInfo.close();
      entry.info.open(map, entry.marker);
      openInfo = entry.info;
      map.panTo(entry.position);
    }

    items.forEach(function (it) {
      var lat = parseFloat(it.dataset.lat);
      var lng = parseFloat(it.dataset.lng);
      if (isNaN(lat) || isNaN(lng)) return;
      var position = new kakao.maps.LatLng(lat, lng);
      var marker = new kakao.maps.Marker({ map: map, position: position });
      var info = new kakao.maps.InfoWindow({ content: buildInfo(it), removable: true });
      var entry = { el: it, marker: marker, info: info, position: position, category: it.dataset.category };
      kakao.maps.event.addListener(marker, 'click', function () { show(entry); });
      if (it.tagName === 'LI') it.addEventListener('click', function () { show(entry); });
      bounds.extend(position);
      entries.push(entry);
    });

    if (entries.length === 1) {
      map.setCenter(entries[0].position);
      map.setLevel(4);
    } else if (entries.length > 1) {
      map.setBounds(bounds);
    }

    // 분류 필터 (지도 페이지에만 존재)
    var filter = document.getElementById('category-filter');
    if (filter) {
      filter.addEventListener('change', function () {
        if (openInfo) { openInfo.close(); openInfo = null; }
        entries.forEach(function (e) {
          var visible = !filter.value || e.category === filter.value;
          e.marker.setMap(visible ? map : null);
          e.el.style.display = visible ? '' : 'none';
        });
      });
    }
  });
})();
