// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';


// 從後端抓資料
const fetchNewsData = async (id) => {
	try {
		const url = `http://${ip}:8080/tinglinews/news/${id}`;
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		renderNewsData(data);
		renderRelatedData(data.relatedNews);
	} catch (e) {
		console.error(e);
	}
};

const fetchFavoriteData = async (id) => {
	try{
		const url = `http://${ip}:8080/tinglinews/users/${id}/favorites`;
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		console.log(state, message, data);
		const topThreeFavorites = data.slice(0, 3); // Select only the top 3 favorite articles
		renderFavoriteData(topThreeFavorites);
	} catch (e) {
		console.error(e);
	}
}

const setImageFormat = (imageData) => {
	if(!imageData) return '';
	let imageFormat = 'jpeg'; // 默認為 jpeg
	if (imageData.startsWith('/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAoHBwkHBgoICAgLCg8LDhgQDg0NDh0VFhEYITIjJh0pKycyMTI0GyUoKDcwJzgsLCkqLjYxNTU1HyY3Pi0zP')) {
		imageFormat = 'png';
	}
	return `data:image/${imageFormat};base64,${imageData}`;
}

const renderNewsData = (data) => {

	// Base64 字串轉圖片
	data.image = setImageFormat(data.image);

	const newsItem = (item) => {
		const journalists = item.journalists.map(journalist => journalist.userName).join(' ');
		return `
			<h1 class="news-title">${item.title}</h1>
			<div class="news-content">           
				<p class="news-date">
					<a href="/tinglinews/list.html?id=${item.tag.tagId}" class="btn btn-outline-secondary btn-sm me-2">${item.tag.tagName}</a>
					<small>記者 ${journalists}&emsp;發布時間 ${item.publicTime}&emsp;更新時間 ${item.updatedTime}</small>
				</p>
				<figure class="news-img-container">
					<img src="${item.image}" class="news-img">
				</figure>
				<div class="news-paragraph">${item.content}</div>
			</div>
 		`;
	};

	$('.news-container').html(Array.isArray(data) ? data.map(newsItem).join('') : newsItem(data));

}

const renderRelatedData = (data) => {

	data.map((item) => {
		const contentContainer = $('<p>').html(item.content);
		const truncatedContent = contentContainer.text().substring(0, 80);
		item.content = contentContainer.text().length > 80 ? truncatedContent + '...' : truncatedContent;

		item.image = setImageFormat(item.image);
	})

	// data.relatedNews.title | content | publicTime | image
	const relatedItem = (item) => `
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
                      <img src="${item.image}" class="list-img">
                    </div>
                  </div>
                </div>
              </a>
            </li>`;

	$('#related-list').html(Array.isArray(data) ? data.map(relatedItem).join('') : relatedItem(data));
}

const renderFavoriteData = (data) => {

	data.map((item) => {
		const contentContainer = $('<p>').html(item.news.content);
		const truncatedContent = contentContainer.text().substring(0, 80);
		item.news.content = contentContainer.text().length > 80 ? truncatedContent + '...' : truncatedContent;

		item.news.image = setImageFormat(item.news.image);
	})

	// data.relatedNews.title | content | publicTime | image
	const favoriteItem = ({news}) => `
		    <li class="list-group-item list-group-item-action">
              <a href="/tinglinews/news.html?id=${news.newsId}">
                <div class="list-info">
                  <div class="row">
                    <div class="col-9">
                      <h4>${news.title}</h4>
                      <p class="content">${news.content}</p>
                      <p class="date">${news.publicTime}</p>
                    </div>
                    <div class="col-3">
                      <img src="${news.image}" class="list-img">
                    </div>
                  </div>
                </div>
              </a>
            </li>`;

	$('#favorite-list').html(Array.isArray(data) ? data.map(favoriteItem).join('') : favoriteItem(data));
}

const handleSubmit = async (event) => {
	event.preventDefault();

	const data = JSON.parse(sessionStorage.getItem('userData'));

	if (!data) {
		Swal.fire('登入解鎖收藏功能', '', 'info');
		return;
	}

	const formData = {
		userId: data.userId,
		newsId: $('#newsId').val()
	};

	try {
		const response = await fetch('http://${ip}:8080/tinglinews/users/favorites', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		if (state) {
			Swal.fire(message, '可至個人資料頁面查看收藏紀錄', 'success');
		} else {
			Swal.fire(message, '', 'warning');
		}
	} catch (error) {
		console.error('收藏發生錯誤：', error);
		Swal.fire('收藏發生錯誤 請稍後再試', error, 'error');
	}
}

// 待 DOM 加載完成之後再執行
$(document).ready(() => {

	const data = JSON.parse(sessionStorage.getItem('userData'));

	if (data) {
		$('.header-container').load('nav-login.html');
		$('#saved-btn').text('收藏這篇文章！');
		fetchFavoriteData(data.userId);
	} else {
		$('.header-container').load('nav.html');
		$('#saved-btn').text('登入收藏這篇文章！');
		$('.favorite-articles-container').css('display', 'none');
	}
	$('.footer-container').load('footer.html');
	$('.ad-container').load('ad.html');

	// 把 uri 抓下來渲染頁面
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);
	const id = urlParams.get('id');
	fetchNewsData(id);

	// 設定收藏按鈕的隱藏欄位
	$('#newsId').val(id);

	$('#saved-form').on('submit', handleSubmit);

});