// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 從後端抓資料
const fetchData = async (uri, id) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/news${uri}`);
		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		data.map((item) => {
			const contentContainer = $('<p>').html(item.content);
			const truncatedContent = contentContainer.text().substring(0, 80);
			item.content = contentContainer.text().length > 80 ? truncatedContent + '...' : truncatedContent;
		})
		// console.log(state, message, data);

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

	for (const item of data) {
		if (item.image) {
			// 檢查圖片格式並動態設置
			let imageFormat = 'jpeg'; // 默認為 jpeg
			if (item.image.startsWith('/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAoHBwkHBgoICAgLCg8LDhgQDg0NDh0VFhEYITIjJh0pKycyMTI0GyUoKDcwJzgsLCkqLjYxNTU1HyY3Pi0zP')) {
				imageFormat = 'png';
			}
			item.image = 'data:image/' + imageFormat + ';base64,' + item.image;
		}
	}

	const newsItem = (item) => `
	 <li class="list-group-item list-group-item-action">
		 <a href="/tinglinews/news.html?id=${item.newsId}">
			 <div class="list-info">
				 <div class="row">
				 	<div class="col-md-3 col-12">
                      <img src="${item.image}" class="list-img">
                    </div>
					<div class="col-md-9 col-12">
						 <h4>${item.title}</h4>
						 <p class="content">${item.content}</p>
						 <p class="date">${item.publicTime}</p>
					</div>
				  </div>
			 </div>
		 </a>
	 </li>
 `;
	$('#news-list').html(Array.isArray(data) ? data.map(newsItem).join('') : newsItem(data));

}


$(document).ready(() => {

	if (sessionStorage.getItem('userData')) {
		$('.header-container').load('./nav-login.html');
	} else {
		$('.header-container').load('./nav.html');
	}
	$('.footer-container').load('./footer.html');
	$('.ad-container').load('./ad.html');

	const queryString = window.location.search;
	// // console.log(`QueryString: ${queryString}`);
	const urlParams = new URLSearchParams(queryString);
	// // console.log(`URLParams: ${urlParams}`);
	const id = urlParams.get('id');
	// console.log(`id: ${id}`);
	if (id != null) {
		fetchData(`/list/${id}`, id);
	} else {
		fetchData('', '');
	}

});
