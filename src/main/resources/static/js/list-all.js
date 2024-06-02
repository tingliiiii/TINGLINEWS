// 從後端抓資料
const fetchData = async (uri) => {
	const url = `http://localhost:8080/tinglinews/news${uri}`;
	try {
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		console.log(state, message, data);
		data.map((item) => {
			const contentContainer = $('<p>').html(item.content);
			const truncatedContent = contentContainer.text().substring(0, 100);
			item.content = contentContainer.text().length > 100 ? truncatedContent + '...' : truncatedContent;
		})
		console.log(state, message, data);
		renderData(data);
	} catch (e) {
		console.error(e);
	}
};

const renderData = (data) => {

	const newsItem = (item) => `
	 <li class="list-group-item list-group-item-action">
		 <a href="/tinglinews/news/${item.newsId}">
			 <div class="list-info">
				 <div class="row">
					 <div class="col-9">
						 <h4>${item.title}</h4>
						 <p class="content">${item.content}</p>
						 <p class="date">${item.publicTime}</p>
					 </div>
					 <div class="col-3">
                      <img src="https://picsum.photos/300/200?random=${item.newsId}" class="list-img">
                    </div>
				 </div>
			 </div>
		 </a>
	 </li>
 `;

	$('#news-list').html(Array.isArray(data) ? data.map(newsItem).join('') : newsItem(data));

}


$(document).ready(() => {

	if (sessionStorage.getItem('userId') != null) {
		$('.header-container').load('../nav-login.html');
	} else {
		$('.header-container').load('../nav.html');
	}
	$('.footer-container').load('../footer.html');
	$('.ad-container').load('../ad.html');

	fetchData('');

});
