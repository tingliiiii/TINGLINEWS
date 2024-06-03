// 從後端抓資料
const fetchData = async (id) => {
	const url = `http://localhost:8080/tinglinews/news/list/${id}`;
	try {
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		console.log(state, message, data);
		data.map((item) => {
			const contentContainer = $('<p>').html(item.content);
			const truncatedContent = contentContainer.text().substring(0, 80);
			item.content = contentContainer.text().length > 80 ? truncatedContent + '...' : truncatedContent;
		})
		console.log(state, message, data);

			if (id != null) {
				switch (id) {
					case '1':
						$('.title').text('政治');
						document.title += '｜政治';
						break;
					case '2':
						$('.title').text('社會');
						document.title += '｜社會';
						break;
					case '3':
						$('.title').text('國際');
						document.title += '｜國際';
						break;
					case '4':
						$('.title').text('環境');
						document.title += '｜環境';
						break;
					case '5':
						$('.title').text('文化');
						document.title += '｜文化';
						break;
					case '6':
						$('.title').text('生活');
						document.title += '｜生活';
						break;
					case '7':
						$('.title').text('娛樂');
						document.title += '｜娛樂';
						break;
					default:
						$('.title').text('即時');
						document.title += '｜即時';
						break;
				}
			}

		renderData(data);
	} catch (e) {
		console.error(e);
	}
};

const renderData = (data) => {

	const newsItem = (item) => `
	 <li class="list-group-item list-group-item-action">
		 <a href="/tinglinews/news.html?id=${item.newsId}">
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
		$('.header-container').load('./nav-login.html');
	} else {
		$('.header-container').load('./nav.html');
	}
	$('.footer-container').load('./footer.html');
	$('.ad-container').load('./ad.html');

	const queryString = window.location.search;
	// console.log(`QueryString: ${queryString}`);
	const urlParams = new URLSearchParams(queryString);
	// console.log(`URLParams: ${urlParams}`);
	const id = urlParams.get('id');
	console.log(`id: ${id}`);
	if (id != null) {
		fetchData(id);
	} else {
		fetchData('');
	}

});
