$(document).ready(function($) {
	"use strict";

	var fount_on_mobile = is_mobile() === true ? true : false,
		fount_on_iPad = navigator.userAgent.match(/iPad/i) != null,
		sidebar_is_open = false;

	$(window).scrollTop(0);

	//Init Banner FullScreen
	banner_resize();

	//Init sidebar
	init_sidebar(sidebar_is_open);

	//Init Portfolio Loader
	if ($("#ajax-section").length > 0)
		initializePortfolio();

	//Onepage navigation
	if ($('.hidden-bar-inner').length > 0) $('.hidden-bar-inner').singlePageNav({ 'offset': $('.hidden-bar-inner .menu').attr('data-offset'), 'filter': '.onepage' });
	if ($('.slider-scroll-button').length > 0) $('.slider-scroll-button').singlePageNav({ 'offset': 0, 'filter': '.onepage' });
	if ($('.sf-menu').length > 0) $('.sf-menu').singlePageNav({ 'offset': parseInt($('.sf-menu').attr('data-offset'), 10), 'filter': '.onepage' });

	if (($("body, html").scrollTop() == 0) && ($(".onepage").length > 0)) {
		$('#menu-section .sf-menu').find('li').children('a').removeClass('current');
		$('#menu-section .sf-menu').children('li').first().children('a').addClass('current');
	}

	$('.menu-toggle').on('click', function() {
		$("#menu-section .sf-menu").slideToggle("slow");
		$(this).toggleClass("active");
	});

	portfolioBgColor();
	portfolioPopup();

	$(document).ajaxComplete(function(event, request, settings) {
		portfolioBgColor();
		portfolioPopup();
	});

	//Countdown Timer
	if ($(".pl-clock").length > 0) {
		$(".pl-clock").each(function() {
			var time = $(this).attr("data-time");
			$(this).countdown(time, function(event) {
				var $this = $(this).html(event.strftime('' + '<div class="countdown-item"><div class="countdown-item-value">%D</div><div class="countdown-item-label">Days</div></div>' + '<div class="countdown-item"><div class="countdown-item-value">%H</div><div class="countdown-item-label">Hours</div></div>' + '<div class="countdown-item"><div class="countdown-item-value">%M</div><div class="countdown-item-label">Minutes</div></div>' + '<div class="countdown-item"><div class="countdown-item-value">%S</div><div class="countdown-item-label">Seconds</div></div>'));
			});
		});
	}

	//Counter
	if ($('.counter-wraper').length > 0) {
		$('.counter-wraper').each(function(index) {
			var $this = $(this);
			var waypoint = $this.waypoint({
				handler: function(direction) {
					$this.find('.counter-digit:not(.counted)').countTo().addClass('counted');
				},
				offset: "90%"
			});
		});
	}

	//ProgressBar
	$('.group-progressbar').each(function() {
		var $this = $(this);
		var waypoint = $this.waypoint({
			handler: function(direction) {
				$this.find('.progressbar').progressbar({ display_text: 'center' });
			},
			offset: "80%"
		});
	});

	//Scrolling Animation
	if ($('.wow').length > 0) Animation();

	$(window).scroll(function() {
		if ($(this).scrollTop() > 300) {
			$('#page-wrapper').addClass('scrolling-menu');
		} else {
			$('#page-wrapper').removeClass('scrolling-menu');
		}

		//Overlay footer
		var footer_height = $("#footer.reveal > .footer-inner").outerHeight();
		$("#footer.reveal").css("height", footer_height);
		if ($(window).scrollTop() + $(window).height() == $(document).height()) {
			$("#footer.reveal").css("z-index", "0");
		} else {
			$("#footer.reveal").css("z-index", "-1");
		}

		//Show or Hide back to top button
		if ($(window).scrollTop() >= 180) {
			$('#fount-to-top').addClass('fount_shown');
		} else {
			$('#fount-to-top').removeClass('fount_shown');
		}
	});

	$(window).load(function() {
		$(window).trigger('hashchange');
		
		//Auto scroll to section
		if(!fount_on_mobile && !fount_on_iPad) {
			if (!$(window.location)[0].hash.includes("#!")) {
				var hash = $(window.location.hash);
				$('a[href="' + hash.selector + '"]').trigger("click");
			}
		}

		$(".noo-spinner").fadeOut('slow').remove();
	});

	$('body').on('click', '#fount-to-top', function() {
		$("html, body").animate({
			scrollTop: 0
		}, 800);
		return false;
	});

	//fitvids
	if ($('.media').legnth > 0) $('.media').fitVids();

	//Load Google Map
	GoogleMap();

	//TOP BAR MENU STUFF
	if ($('ul.sf-menu').length > 0) {
		$('#nav-main ul.sf-menu').supersubs({
			minWidth: 1,
			maxWidth: 12,
			extraWidth: 2
		}).superfish({
			hoverClass: 'fount_hover_sub',
			delay: 100,
			animation: { color: 'show' },
			cssArrows: false,
			speed: 250,
			speedOut: 250,
			dropShadows: false,
			onBeforeShow: function() {
				$(this).css({ 'visibility': 'visible' });
			},
			onHide: function() {
				$(this).css({ 'visibility': 'hidden' });
			}
		});
	}

	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	/* Fullscreen Banner Height  */
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

	introHeight();
	$(window).bind('resize', function() {
		//Update slider height on resize
		introHeight();
		banner_resize();
	});

	initRotationText();

	if (!fount_on_mobile || fount_on_iPad) {
		$('#fullscreen-banner').imagesLoaded(function() {
			fullScreenSlider(fount_on_mobile);
			setTimeout(function() {
				$(window).trigger("debouncedresize");
			}, 300);
		});
	} else {
		fullScreenSlider(fount_on_mobile);
		setTimeout(function() {
			$(window).trigger("debouncedresize");
		}, 300);
	}

	//Init Logo Header
	initLogoHeader();

	OwlCarousel();
	
	$('.maps').on('click', function() {
		$('.maps iframe').css("pointer-events", "auto");
	});
	$('.maps').on('mouseleave', function() {
	  $('.maps iframe').css("pointer-events", "none"); 
	});
});
