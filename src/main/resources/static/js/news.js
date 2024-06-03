// 從後端抓資料
const fetchData = async (id) => {
	const url = `http://localhost:8080/tinglinews/news/${id}`;
	try {
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		console.log(state, message, data);
		renderData(data);
	} catch (e) {
		console.error(e);
	}
};

const renderData = (data) => {

	const newsItem = (item) => `
	<h3 class="news-title">${item.title}</h3>
            <div class="news-content">
              <p class="news-date">
                <a href="/tinglinews/list.html?id=${item.tag.tagId}" class="news-tags">${item.tag.tagName}</a>
                <small>記者 ${item.userName}&emsp;發布時間 ${item.publicTime}&emsp;更新時間 ${item.updatedTime}</small>
              </p>
              <p class="news-paragraph">${item.content}</p>
 `;

	$('.news-container').html(Array.isArray(data) ? data.map(newsItem).join('') : newsItem(data));

}



// 待 DOM 加載完成之後再執行
$(document).ready(() => {
	if (sessionStorage.getItem('userId') != null) {
		$('.header-container').load('nav-login.html');
	} else {
		$('.header-container').load('nav.html');
	}
	$('.footer-container').load('footer.html');
	$('.ad-container').load('ad.html');

	const queryString = window.location.search;
	console.log(`QueryString: ${queryString}`);
	const urlParams = new URLSearchParams(queryString);
	console.log(`URLParams: ${urlParams}`);
	const id = urlParams.get('id');
	console.log(`ID 參數: ${id}`);
	fetchData(id);

});
