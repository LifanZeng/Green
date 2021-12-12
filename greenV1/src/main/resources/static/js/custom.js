function introHeight() {
	var wh = $(window).height();
	$('#fullscreen-banner .fount-banner').css({ height: wh });
	$('.section-full').css({ height: wh });
}

function Animation() {
	var wow = new WOW({
		boxClass: 'wow', // default
		animateClass: 'animated', // default
		offset: 0, // default
		mobile: true, // default
		live: true // default
	});
	wow.init();
}

function portfolioPopup() {
	//Ajax popup
	if ($(".popup").length > 0) {
		$('.popup').magnificPopup({
			type: 'ajax',
			// Delay in milliseconds before popup is removed
			removalDelay: 300,

			// Class that is added to popup wrapper and background
			// make it unique to apply your CSS animations just to this exact popup
			mainClass: 'mfp-fade'
		});
	}

	//Gallery popup
	if ($(".gallery-item").length > 0) {
		$('.gallery-item').magnificPopup({
			type: 'image',
			gallery: {
				enabled: true
			}
		});
	}
}

function portfolioBgColor() {
	//Portfolio Masonry Background
	$('.folio-masonry .folio-item').each(function() {
		var color = $(this).attr("data-color");
		$(this).find('.folio-overlay').css('background-color', color);
	});
}

function GoogleMap() {
	// When the window has finished loading create our google map below
	var marker_image = "../images/assets/map-marker.png";

	if ($('#map').length > 0) {
		if ($('#map').attr('data-marker-image') != undefined) {
			marker_image = $('#map').attr('data-marker-image')
		}
		google.maps.event.addDomListener(window, 'load', init);
	}

	function init() {
		// Basic options for a simple Google Map
		// For more options see: https://developers.google.com/maps/documentation/javascript/reference#MapOptions
		var mapOptions = {
			// How zoomed in you want the map to start at (always required)
			zoom: 11,
			scrollwheel: false,

			// The latitude and longitude to center the map (always required)
			center: new google.maps.LatLng(40.6700, -73.9400), // New York

			// How you would like to style the map.
			// This is where you would paste any style found on Snazzy Maps.
			styles: [{
				"featureType": "water",
				"elementType": "geometry.fill",
				"stylers": [{ "color": "#d3d3d3" }]
			}, {
				"featureType": "transit",
				"stylers": [{ "color": "#808080" }, { "visibility": "off" }]
			}, {
				"featureType": "road.highway",
				"elementType": "geometry.stroke",
				"stylers": [{ "visibility": "on" }, { "color": "#b3b3b3" }]
			}, {
				"featureType": "road.highway",
				"elementType": "geometry.fill",
				"stylers": [{ "color": "#ffffff" }]
			}, {
				"featureType": "road.local",
				"elementType": "geometry.fill",
				"stylers": [{ "visibility": "on" }, { "color": "#ffffff" }, { "weight": 1.8 }]
			}, {
				"featureType": "road.local",
				"elementType": "geometry.stroke",
				"stylers": [{ "color": "#d7d7d7" }]
			}, {
				"featureType": "poi",
				"elementType": "geometry.fill",
				"stylers": [{ "visibility": "on" }, { "color": "#ebebeb" }]
			}, {
				"featureType": "administrative",
				"elementType": "geometry",
				"stylers": [{ "color": "#a7a7a7" }]
			}, {
				"featureType": "road.arterial",
				"elementType": "geometry.fill",
				"stylers": [{ "color": "#ffffff" }]
			}, {
				"featureType": "road.arterial",
				"elementType": "geometry.fill",
				"stylers": [{ "color": "#ffffff" }]
			}, {
				"featureType": "landscape",
				"elementType": "geometry.fill",
				"stylers": [{ "visibility": "on" }, { "color": "#efefef" }]
			}, {
				"featureType": "road",
				"elementType": "labels.text.fill",
				"stylers": [{ "color": "#696969" }]
			}, {
				"featureType": "administrative",
				"elementType": "labels.text.fill",
				"stylers": [{ "visibility": "on" }, { "color": "#737373" }]
			}, {
				"featureType": "poi",
				"elementType": "labels.icon",
				"stylers": [{ "visibility": "off" }]
			}, {
				"featureType": "poi",
				"elementType": "labels",
				"stylers": [{ "visibility": "off" }]
			}, {
				"featureType": "road.arterial",
				"elementType": "geometry.stroke",
				"stylers": [{ "color": "#d6d6d6" }]
			}, {
				"featureType": "road",
				"elementType": "labels.icon",
				"stylers": [{ "visibility": "off" }]
			}, {}, { "featureType": "poi", "elementType": "geometry.fill", "stylers": [{ "color": "#dadada" }] }]
		};

		// Get the HTML DOM element that will contain your map
		// We are using a div with id="map" seen below in the <body>
		var mapElement = document.getElementById('map');
		// Create the Google Map using our element and options defined above
		var map = new google.maps.Map(mapElement, mapOptions);

		// Let's also add a marker while we're at it
		var marker = new google.maps.Marker({
			position: new google.maps.LatLng(40.6000, -73.9400),
			map: map,
			title: 'Megatron',
			icon: marker_image
		});

		var marker2 = new google.maps.Marker({
			position: new google.maps.LatLng(40.6800, -73.8000),
			map: map,
			title: 'Megatron',
			icon: marker_image
		});

		var marker3 = new google.maps.Marker({
			position: new google.maps.LatLng(40.7300, -74.1280),
			map: map,
			title: 'Megatron',
			icon: marker_image
		});

	}
}

/*----------------------------------------------------*/
// LOAD PROJECT
/*----------------------------------------------------*/



function initializePortfolio() {

	var current,
		next,
		prev,
		target,
		hash,
		url,
		page,
		title,
		projectIndex,
		scrollPostition,
		projectLength,
		ajaxLoading = false,
		wrapperHeight,
		pageRefresh = true,
		content = false,
		loader = $('div#loader'),
		portfolioGrid = $('div#portfolio-wrap'),
		projectContainer = $('div#ajax-content-inner'),
		projectNav = $('#top-bar-wrapper'),
		//exitProject = $('div#closeProject a'),
		easing = 'easeOutExpo',
		folderName = 'ajax/inline';

	$(window).bind('hashchange', function() {

		hash = $(window.location).attr('hash');
		var root = '#!' + folderName + '/';
		var rootLength = root.length;


		if (hash.substr(0, rootLength) != root) {
			return;
		} else {

			var correction = 50;
			var headerH = $('nav').outerHeight() + correction;
			hash = $(window.location).attr('hash');
			url = hash.replace(/[#\!]/g, '');


			portfolioGrid.find('div.folio-item.current').removeClass('current');


			/* IF URL IS PASTED IN ADDRESS BAR AND REFRESHED */
			if (pageRefresh == true && hash.substr(0, rootLength) == root) {

				$('html,body').stop().animate({ scrollTop: (projectContainer.offset().top - 20) + 'px' }, 800, 'easeOutExpo', function() {
					loadProject();
				});

				/* CLICKING ON PORTFOLIO GRID OR THROUGH PROJECT NAVIGATION */
			} else if (pageRefresh == false && hash.substr(0, rootLength) == root) {
				$('html,body').stop().animate({ scrollTop: (projectContainer.offset().top - headerH) + 'px' }, 800, 'easeOutExpo', function() {

					if (content == false) {
						loadProject();
					} else {
						projectContainer.animate({ opacity: 0, height: wrapperHeight }, function() {
							loadProject();
						});
					}

					projectNav.fadeOut('100');
					//exitProject.fadeOut('100');

				});

				/* USING BROWSER BACK BUTTON WITHOUT REFRESHING */
			} else if (hash == '' && pageRefresh == false || hash.substr(0, rootLength) != root && pageRefresh == false || hash.substr(0, rootLength) != root && pageRefresh == true) {
				scrollPostition = hash;
				$('html,body').stop().animate({ scrollTop: scrollPostition + 'px' }, 1000, function() {

					deleteProject();

				});

				/* USING BROWSER BACK BUTTON WITHOUT REFRESHING */
			}



			/* ADD ACTIVE CLASS TO CURRENTLY CLICKED PROJECT */
			if ($('.folio-icons-wrap').length > 0) {
				portfolioGrid.find('div.folio-item .folio-icons-wrap a.ajax-inline[href="#!' + url + '"]').parent().parent().parent().addClass('current');
			} else {
				portfolioGrid.find('div.folio-item a.ajax-inline[href="#!' + url + '"]').parent().parent().addClass('current');
			}
		}

	});
	/* LOAD PROJECT */
	function loadProject() {
		loader.fadeIn().removeClass('projectError').html('');


		if (!ajaxLoading) {
			ajaxLoading = true;

			projectContainer.load(url + ' div#page-wrapper', function(xhr, statusText, request) {

				if (statusText == "success") {

					ajaxLoading = false;

					page = $('div#page-wrapper');

					$('#page-wrapper').waitForImages(function() {
						hideLoader();
					});

					$(".container").fitVids();

				}

				if (statusText == "error") {

					loader.addClass('projectError').append(loadingError);

					loader.find('p').slideDown();

				}

			});

		}

	}

	function hideLoader() {
		loader.fadeOut('fast', function() {
			showProject();
		});
	}

	function showProject() {
		wrapperHeight = projectContainer.children('div#page-wrapper').outerHeight() + 'px';
		projectContainer.animate({ opacity: 1, height: wrapperHeight }, function() {
			$(".container").fitVids();
			scrollPostition = $('html,body').scrollTop();
			projectNav.fadeIn();

		});


		projectIndex = portfolioGrid.find('div.folio-item.current').index();
		projectLength = $('div.folio-item').length - 1;


		if (projectIndex == projectLength) {

			$('#fount-right a').addClass('disabled');
			$('#fount-left a').removeClass('disabled');

		} else if (projectIndex == 0) {

			$('#fount-left a').addClass('disabled');
			$('#fount-right a').removeClass('disabled');

		} else {

			$('#fount-left a,#fount-right a').removeClass('disabled');

		}

	}

	function deleteProject(closeURL) {
		projectNav.fadeOut(100);
		//exitProject.fadeOut(100);
		projectContainer.animate({ opacity: 0, height: '0px' });
		projectContainer.empty();

		if (typeof closeURL != 'undefined' && closeURL != '') {
			location = '#!';
		}
		portfolioGrid.find('div.folio-item.current').removeClass('current');
	}


	/* LINKING TO PREIOUS AND NEXT PROJECT VIA PROJECT NAVIGATION */
	$('#fount-right').on('click', function() {

		current = portfolioGrid.find('.folio-item.current');
		next = current.next('.folio-item');
		target = $(next).find('.ajax-inline').attr('href');
		$(this).find('a').attr('href', target);


		if (next.length === 0) {
			return false;
		}

		current.removeClass('current');
		next.addClass('current');

	});

	$('#fount-left').on('click', function() {

		current = portfolioGrid.find('.folio-item.current');
		prev = current.prev('.folio-item');
		target = $(prev).find('.ajax-inline').attr('href');
		$(this).find('a').attr('href', target);


		if (prev.length === 0) {
			return false;
		}

		current.removeClass('current');
		prev.addClass('current');

	});


	/* CLOSE PROJECT */
	$('#squared-close').on('click', function() {

		deleteProject($(this).attr('data-href'));
		loader.fadeOut();
		return false;

	});
	pageRefresh = false;
}

function initRotationText() {
	if ($('#js-rotating').length > 0) {
		$("#js-rotating").Morphext({
			animation: "fadeInDown",
			// An array of phrases to rotate are created based on this separator. Change it if you wish to separate the phrases differently (e.g. So Simple | Very Doge | Much Wow | Such Cool).
			separator: ",",
			// The delay between the changing of each phrase in milliseconds.
			speed: 2000,
			complete: function() {
				// Called after the entrance animation is executed.
			}
		});
	}

	if ($('#js-rotating-2').length > 0) {
		$("#js-rotating-2").Morphext({
			animation: "flipInX",
			separator: ",",
			speed: 2000,
			complete: function() {}
		});
	}
}

function hasParentClass(e, classname) {
	if (e === document) {
		return false;
	}
	if (classie.has(e, classname)) {
		return true;
	}
	return e.parentNode && hasParentClass(e.parentNode, classname);
}

function init_sidebar(sidebar_is_open) {
	$('.hidden-bar-toggle').on('click', function(e) {
		prk_toggle_sidebar(sidebar_is_open);
	});
	$('.sidebar_opener a').on('click', function(e) {
		e.preventDefault();
		prk_toggle_sidebar(sidebar_is_open);
	});
}

function prk_toggle_sidebar(sidebar_is_open) {
	if (sidebar_is_open === false) {
		$('.hidden-bar-toggle').removeClass('hover_trigger');
		sidebar_is_open = true;
		$('body').addClass('prk_shifted');
		$('.hidden-bar').css({ 'visibility': 'visible' });
		setTimeout(function() {
			document.addEventListener("click", function(evt) {
				console.log(evt);
				if (evt === 'close_flag' || hasParentClass(evt.target, 'hider_flag')) {
					if (sidebar_is_open === true) {
						prk_toggle_sidebar(sidebar_is_open);
					}
				}
				if (evt.target.className.includes("onepage")) {
					if (sidebar_is_open === true) {
						prk_toggle_sidebar(sidebar_is_open);
					}
				}
			});
			$('#body_hider').addClass('prk_shifted_hider');
			$('body').addClass('showing_hidden_sidebar');
		}, 300);
	} else {
		sidebar_is_open = false;
		$('body').removeClass('prk_shifted');
		$('body').removeClass('showing_hidden_sidebar');
		$('#body_hider').removeClass('prk_shifted_hider');
		setTimeout(function() {
			document.addEventListener("click", function(evt) {
				if (evt === 'close_flag' || hasParentClass(evt.target, 'hider_flag')) {
					if (sidebar_is_open === true) {
						prk_toggle_sidebar(sidebar_is_open);
					}
				}
				if (evt.target.className.includes("onepage")) {
					if (sidebar_is_open === true) {
						prk_toggle_sidebar(sidebar_is_open);
					}
				}
			});
			$('.hidden-bar').css({ 'visibility': 'hidden' });
		}, 300);
	}
}

function is_mobile() {
	var check = false;
	(function(a) {
		if ((/android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i.test(navigator.userAgent.toLowerCase())) || /(android|ipad|playbook|silk|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(a.toLowerCase()) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0, 4).toLowerCase())) { check = true; }
	})(navigator.userAgent || navigator.vendor || window.opera);
	return check;
}

function banner_resize() {
	if ($.browser.msie && parseInt($.browser.version, 10) === 8) {
		height_fix = $(window).height();
	} else {
		height_fix = window.innerHeight ? window.innerHeight : $(window).height();
	}
	$(".hidden-bar,.hidden-bar_scroller").outerHeight(height_fix);
}

function fullScreenSlider(fount_on_mobile) {
	var autoplay_enable = 0;
	$('.super_height').each(function() {
		var $this_slider = $(this);
		$(window).on("debouncedresize", function(event) {
			setTimeout(function() {
				if ($('body').hasClass('menu_at_top')) {
					$this_slider.find('.owl-wrapper-outer,.owl-item').css({ 'height': height_fix - ($('.header-logo').height()) });
				} else {
					$this_slider.find('.owl-wrapper-outer,.owl-item').css({ 'height': height_fix - (parseInt($('#fullscreen-banner').css('padding-top'), 10)) + 2 });
				}
				var min_width = $(window).width();
				var min_height = height_fix - (parseInt($('#fullscreen-banner').css('padding-top'), 10)) + 2;
				$this_slider.find('.owl-item img.fount_vsbl').each(function() {
					var $this_image = $(this);
					var or_width = parseInt($this_image.attr('data-or_w'), 10);
					var or_height = parseInt($this_image.attr('data-or_h'), 10);
					var ratio = min_height / or_height;
					//FILL HEIGHT
					$this_image.css("height", min_height);
					$this_image.css("width", or_width * ratio);
					//UPDATE VARS
					or_width = $this_image.width();
					or_height = $this_image.height();
					//FILL WIDTH IF NEEDED
					if (or_width < min_width) {
						ratio = min_width / or_width;
						$this_image.css("width", min_width);
						$this_image.css("height", or_height * ratio);
					}
					//ADJUST MARGINS
					$this_image.css({ "margin-left": -($this_image.width() - min_width) / 2 });
					if ($(window).width() < 780) {
						$this_image.css("margin-top", 0);
					} else {
						$this_image.css("margin-top", -($this_image.height() - $this_slider.find('.owl-wrapper-outer').height()) / 2);
					}
				});
				$this_slider.find('.sld-v-center').each(function() {
					$(this).css({ 'margin-top': -parseInt($(this).height() / 2, 10) });
				});
			}, 50);
		});
		if ($this_slider.find('.item').length > 1 && $this_slider.attr('data-autoplay') === "true") {
			var autoplayer = $this_slider.attr('data-delay');
			if (fount_on_mobile && autoplay_enable !== "1") {
				autoplayer = false;
			}
		} else {
			var autoplayer = false;
		}
		$this_slider.fitVids().owlCarousel({
			autoPlay: autoplayer,
			navigation: $this_slider.attr('data-navigation') === "true" ? true : false,
			navigationText: ['<i class="icon-left-open-big"></i>', '<i class="icon-right-open-big"></i>'],
			pagination: $this_slider.attr('data-pagination') === "true" ? true : false,
			slideSpeed: 300,
			paginationSpeed: 400,
			items: 1,
			lazyLoad: false,
			itemsDesktop: false,
			itemsDesktopSmall: false,
			itemsTablet: false,
			itemsMobile: false,
			itemsScaleUp: true,
			transitionStyle: "fade",
			touchDrag: $this_slider.attr('data-touch') === "true" ? true : false,
			addClassActive: true,
			afterInit: function() {
				setTimeout(function() {
					setTimeout(function() {
						$this_slider.find('.sld-v-center').each(function() {
							$(this).css({ 'margin-top': -parseInt($(this).height() / 2, 10) });
						});
					}, 10);
					//LOAD ALL OTHER IMAGES NOW
					$this_slider.find('.lazyOwl').each(function() {
						$(this).attr('src', $(this).attr('data-src'));
						$(this).css({ 'display': 'block' });
					});
				}, 750);
				$this_slider.find('.owl-pagination').css({ 'margin-top': -$this_slider.find('.owl-pagination').height() / 2 });
				$(window).trigger("debouncedresize");
			},
			afterAction: function() {
				$this_slider.find('.headings-top,.headings-body,.slider_action_button').removeClass('fount_animate_slide');
				var slide_id = '#fount_slide_' + this.owl.currentItem + '';
				if ($this_slider.hasClass('just_init')) {
					var in_count = 750;
					$this_slider.removeClass('just_init');
				} else {
					var in_count = 0;
				}
				setTimeout(function() {
					if ($this_slider.find(slide_id).find('.slider_action_button a').attr('data-color') !== "default") {
						if ($('#fount_wrapper').hasClass('solid_buttons')) {
							$this_slider.find(slide_id).find('.slider_action_button a').css({ 'background-color': $this_slider.find(slide_id).find('.slider_action_button a').attr('data-color') });
						} else {
							$this_slider.find(slide_id).find('.slider_action_button a').css({ 'border-color': $this_slider.find(slide_id).find('.slider_action_button a').attr('data-color'), 'color': $this_slider.find(slide_id).find('.slider_action_button a').attr('data-color') });
						}
					}
					if ($this_slider.find(slide_id).find('.slider-scroll-button a').attr('data-color') !== "default") {
						if ($('#fount_wrapper').hasClass('solid_buttons')) {
							$this_slider.find(slide_id).find('.slider-scroll-button a').css({ 'background-color': $this_slider.find(slide_id).find('.slider-scroll-button a').attr('data-color') });
						} else {
							$this_slider.find(slide_id).find('.slider-scroll-button a').css({ 'border-color': $this_slider.find(slide_id).find('.slider-scroll-button a').attr('data-color'), 'color': $this_slider.find(slide_id).find('.slider-scroll-button a').attr('data-color') });
						}
					}
					$this_slider.find(slide_id).find('.headings-top').addClass('fount_animate_slide');
					$this_slider.find(slide_id).find('.headings-body').addClass('fount_animate_slide');
					$this_slider.find(slide_id).find('.slider_action_button').addClass('fount_animate_slide');
				}, in_count);
			}
		});
	});
}

function initLogoHeader() {
	$('.header-logo-holder').imagesLoaded(function() {
		setTimeout(function() {
			$('#prk_alt_logo_image').css({ 'max-height': $('#prk_alt_logo_image').attr('height') + 'px' });
			$('#prk_logo_image').css({ 'max-height': $('#prk_logo_image').attr('height') + 'px' });
			$('.fount_logo_above.fount_forced_menu #centered_block').css({ 'margin-top': $('#prk_alt_logo_image').attr('height') + 'px' });
			var found_url = false;
			//TRY TO HIGHLIGHT PARENT PAGES 
			if ($('#content').attr('data-parent') !== undefined) {
				if (found_url === false) {
					$('#menu-section .sf-menu>li>a').each(function() {
						if ($(this).attr('href') === $('#content').attr('data-parent')) {
							$(this).parent().addClass('active');
							found_url = true;
						}
					});
				}
				if (found_url === false) {
					$('#menu-section .sf-menu li a').each(function() {
						if ($(this).attr('href') === $('#content').attr('data-parent')) {
							$(this).parent().parent().parent().addClass('active');
							found_url = true;
						}
					});
				}
			}
			if (found_url === false) {
				$('#menu-section .sf-menu li').each(function() {
					if ($(this).hasClass('active') && $(this).parent().hasClass('sub-menu')) {
						$(this).parent().parent().addClass('active');
						found_url = true;
					}
				});
			}
			if (found_url === false && $('.mini-site-header').length) {
				if (window.location.href === $("#menu-section .sf-menu>li:first-child>a").attr('href').split('#')[0]) {
					$("#menu-section .sf-menu>li:first-child").addClass('active');
				} else {
					//MULTIPAGE SEARCH
					$("#menu-section .sf-menu>li>a").each(function() {
						if (window.location.href === $(this).attr('href')) {
							$('#menu-section ul li.active').removeClass('active');
							$(this).parent().addClass('active');
						}
					});
				}
			}
			$('#header-nav').addClass('first-anim');
		}, 50);
	});
}

function OwlCarousel() {
	var fount_on_mobile = is_mobile() === true ? true : false;

	//Testimonial Carousel
	$(".testimonials_slider").each(function() {
		$(this).owlCarousel({
			items: 1,
			loop: true,
			mouseDrag: true,
			nav: false,
			dots: true,
			autoplay: true,
			autoplayTimeout: 5000,
			autoplayHoverPause: true,
			smartSpeed: 1000,
			autoplayHoverPause: true,
			itemsDesktop: [1199, 1],
			itemsDesktopSmall: [979, 1],
			itemsTablet: [768, 1],
			itemsMobile: [479, 1]
		});
	});

	//Post Carousel
	$(".post-slider").each(function() {
		$(this).owlCarousel({
			items: 3,
			loop: true,
			mouseDrag: true,
			nav: true,
			dots: false,
			autoplay: true,
			autoplayTimeout: 5000,
			autoplayHoverPause: true,
			smartSpeed: 1000,
			autoplayHoverPause: true,
			navigationText: ['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>'],
			itemsDesktop: [1199, 3],
			itemsDesktopSmall: [979, 3],
			itemsTablet: [768, 2],
			itemsMobile: [479, 1]
		});
	});

	//Gallery Carousel
	$(".gallery-slider").each(function() {
		$(this).owlCarousel({
			items: 1,
			loop: true,
			mouseDrag: true,
			nav: false,
			dots: false,
			pagination: false,
			autoplay: true,
			autoplayTimeout: 5000,
			autoplayHoverPause: true,
			smartSpeed: 1000,
			autoplayHoverPause: true,
			navigationText: ['<i class="fa fa-angle-left"></i>', '<i class="fa fa-angle-right"></i>'],
			itemsDesktop: [1199, 1],
			itemsDesktopSmall: [979, 1],
			itemsTablet: [768, 1],
			itemsMobile: [479, 1]
		});
	});

	//Member Carousel
	$(".member-ul-slider").each(function() {
		$(this).owlCarousel({
			items: 3,
			loop: true,
			mouseDrag: true,
			nav: false,
			dots: false,
			pagination: false,
			autoplay: true,
			autoplayTimeout: 5000,
			autoplayHoverPause: true,
			smartSpeed: 1000,
			autoplayHoverPause: true,
			navigationText: ['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>'],
			itemsDesktop: [1199, 4],
			itemsDesktopSmall: [979, 3],
			itemsTablet: [768, 2],
			itemsMobile: [479, 1]
		});
	});

	//Product Carousel
	$(".products-ul-slider").each(function() {
		$(this).owlCarousel({
			items: 4,
			loop: true,
			mouseDrag: true,
			nav: false,
			dots: false,
			pagination: false,
			autoplay: true,
			autoplayTimeout: 5000,
			autoplayHoverPause: true,
			smartSpeed: 1000,
			autoplayHoverPause: true,
			navigationText: ['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>'],
			itemsDesktop: [1199, 4],
			itemsDesktopSmall: [979, 3],
			itemsTablet: [768, 2],
			itemsMobile: [479, 1]
		});
	});

	//Twitter Slider
	$('.twitter-slider').each(function() {
		var $this_slider = $(this);
		if (!fount_on_mobile) {
			var autoplayer = true;
		} else {
			var autoplayer = false;
		}
		$this_slider.flexslider({
			animation: "slide",
			useCSS: false,
			slideshow: autoplayer,
			slideshowSpeed: 5000,
			animationDuration: 300,
			smoothHeight: true,
			directionNav: true,
			controlNav: false,
			keyboardNav: false,
			touchDrag: false,
			prevText: '<i class="fa fa-chevron-left prk_less_opacity"></i>',
			nextText: '<i class="fa fa-chevron-right prk_less_opacity"></i>',
			start: function(slider) {
				slider.css({ 'min-height': 0 });
				$(window).trigger("debouncedresize");
			}
		});
	});
}
