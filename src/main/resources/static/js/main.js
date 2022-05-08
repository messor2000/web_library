// Get Color Attribute
// Set the background book color
$("li.book-item").each(function () {
    var $this = $(this);

    $this.find(".bk-front > div").css('background-color', $(this).data("color"));
    $this.find(".bk-left").css('background-color', $(this).data("color"));
    $this.find(".back-color").css('background-color', $(this).data("color"));

    $this.find(".item-details a.button").on('click', function () {
        displayBookDetails($this);
    });
});

function displayBookDetails(el) {
    var $this = $(el);
    $('.main-container').addClass('prevent-scroll');
    $('.main-overlay').fadeIn();

    $this.find('.overlay-details').clone().prependTo('.main-overlay');

    $('a.close-overlay-btn').on('click', function (e) {
        e.preventDefault();
        $('.main-container').removeClass('prevent-scroll');
        $('.main-overlay').fadeOut();
        $('.main-overlay').find('.overlay-details').remove();
    });

    $('.main-overlay a.preview').on('click', function () {
        $('.main-overlay .overlay-desc').toggleClass('activated');
        $('.main-overlay .overlay-preview').toggleClass('activated');
    });

    $('.main-overlay a.back-preview-btn').on('click', function () {
        $('.main-overlay .overlay-desc').toggleClass('activated');
        $('.main-overlay .overlay-preview').toggleClass('activated');
    });
}

/*
 *  Offcanvas Menu
 */
// Open Offcanvas Menu
$('.main-navigation a').on('click', function () {
    $('.main-container').addClass('nav-menu-open');
    $('.main-overlay').fadeIn();
});

// Close Offcanvas Menu
$('.overlay-full').on('click', function () {
    $('.main-container').removeClass('nav-menu-open');
    $('.main-container').removeClass('prevent-scroll');
    $(this).parent().fadeOut();
    $(this).parent().find('.overlay-details').remove();
});

/*
 *  Shuffle.js for Search, Catagory filter and Sort
 */

// Initiate Shuffle.js
var Shuffle = window.shuffle;

var bookList = function (element) {
    this.shuffle = new Shuffle(element, {
        itemSelector: '.book-item',
    });

    this._activeFilters = [];
    this.addFilterButtons();
    this.addSorting();
    this.addSearchFilter();
    this.mode = 'exclusive';
};

bookList.prototype.toArray = function (arrayLike) {
    return Array.prototype.slice.call(arrayLike);
};

// Catagory Filter Functions
// Toggle mode for the Catagory filters
bookList.prototype.toggleMode = function () {
    if (this.mode === 'additive') {
        this.mode = 'exclusive';
    } else {
        this.mode = 'additive';
    }
};

// Filter buttons eventlisteners
bookList.prototype.addFilterButtons = function () {
    var options = document.querySelector('.filter-options');
    if (!options) {
        return;
    }
    var filterButtons = this.toArray(options.children);

    filterButtons.forEach(function (button) {
        button.addEventListener('click', this._handleFilterClick.bind(this), false);
    }, this);
};

// Function for the Catagory Filter
bookList.prototype._handleFilterClick = function (evt) {
    var btn = evt.currentTarget;
    var isActive = btn.classList.contains('active');
    var btnGroup = btn.getAttribute('data-group');

    if (this.mode === 'additive') {
        if (isActive) {
            this._activeFilters.splice(this._activeFilters.indexOf(btnGroup));
        } else {
            this._activeFilters.push(btnGroup);
        }

        btn.classList.toggle('active');
        this.shuffle.filter(this._activeFilters);

    } else {
        this._removeActiveClassFromChildren(btn.parentNode);

        var filterGroup;
        if (isActive) {
            btn.classList.remove('active');
            filterGroup = Shuffle.ALL_ITEMS;
        } else {
            btn.classList.add('active');
            filterGroup = btnGroup;
        }

        this.shuffle.filter(filterGroup);
    }
};

// Remove classes for active states
bookList.prototype._removeActiveClassFromChildren = function (parent) {
    var children = parent.children;
    for (var i = children.length - 1; i >= 0; i--) {
        children[i].classList.remove('active');
    }
};

// Sort By
// Watching for the select box to change to run
bookList.prototype.addSorting = function () {
    var menu = document.querySelector('.sort-options');
    if (!menu) {
        return;
    }
    menu.addEventListener('change', this._handleSortChange.bind(this));
};

// Sort By function Handler runs on change
bookList.prototype._handleSortChange = function (evt) {
    var value = evt.target.value;
    var options = {};

    function sortByDate(element) {
        return element.getAttribute('data-created');
    }

    function sortByTitle(element) {
        return element.getAttribute('data-title').toLowerCase();
    }

    if (value === 'date-created') {
        options = {
            reverse: true,
            by: sortByDate,
        };
    } else if (value === 'title') {
        options = {
            by: sortByTitle,
        };
    }

    this.shuffle.sort(options);
};

// Searching the Data-attribute Title
// Advanced filtering
// Waiting for input into the search field
bookList.prototype.addSearchFilter = function () {
    var searchInput = document.querySelector('.shuffle-search');
    if (!searchInput) {
        return;
    }
    searchInput.addEventListener('keyup', this._handleSearchKeyup.bind(this));
};

// Search function Handler to search list
bookList.prototype._handleSearchKeyup = function (evt) {
    var searchInput = document.querySelector('.shuffle-search');
    var searchText = evt.target.value.toLowerCase();

    // Check if Search input has value to toggle class
    if (searchInput && searchInput.value) {
        $('.catalog-search').addClass('input--filled');
    } else {
        $('.catalog-search').removeClass('input--filled');
    }

    this.shuffle.filter(function (element, shuffle) {

        // If there is a current filter applied, ignore elements that don't match it.
        if (shuffle.group !== Shuffle.ALL_ITEMS) {
            // Get the item's groups.
            var groups = JSON.parse(element.getAttribute('data-groups'));
            var isElementInCurrentGroup = groups.indexOf(shuffle.group) !== -1;

            // Only search elements in the current group
            if (!isElementInCurrentGroup) {
                return false;
            }
        }

        var titleElement = element.querySelector('.book-item_title');
        var titleText = titleElement.textContent.toLowerCase().trim();

        return titleText.indexOf(searchText) !== -1;
    });
};

// Wait till dom load to start the Shuffle js funtionality
document.addEventListener('DOMContentLoaded', function () {
    window.book_list = new bookList(document.getElementById('grid'));
});
