/**
 * Global Theme Manager
 * Manages dark/light theme across all pages
 * Persists theme preference in localStorage
 */

class ThemeManager {
  constructor() {
    this.THEME_KEY = "eventhub-theme";
    this.LIGHT_MODE = "light";
    this.DARK_MODE = "dark";
    this.init();
  }

  /**
   * Initialize theme manager
   */
  init() {
    this.loadTheme();
    this.setupThemeToggle();
  }

  /**
   * Load theme from localStorage and apply it
   */
  loadTheme() {
    const savedTheme = localStorage.getItem(this.THEME_KEY) || this.LIGHT_MODE;
    this.applyTheme(savedTheme);
  }

  /**
   * Apply theme to the document
   */
  applyTheme(theme) {
    const body = document.body;
    const root = document.documentElement;

    if (root) {
      root.setAttribute("data-theme", theme);
    }

    if (theme === this.DARK_MODE) {
      body.classList.add("dark-mode");
    } else {
      body.classList.remove("dark-mode");
    }

    // Update all theme toggle buttons
    this.updateThemeToggleButtons(theme);
  }

  /**
   * Toggle between light and dark theme
   */
  toggleTheme() {
    const currentTheme =
      localStorage.getItem(this.THEME_KEY) || this.LIGHT_MODE;
    const newTheme =
      currentTheme === this.LIGHT_MODE ? this.DARK_MODE : this.LIGHT_MODE;

    localStorage.setItem(this.THEME_KEY, newTheme);
    this.applyTheme(newTheme);
  }

  /**
   * Setup theme toggle button(s)
   */
  setupThemeToggle() {
    const toggleButtons = document.querySelectorAll(".theme-toggle");
    toggleButtons.forEach((button) => {
      button.addEventListener("click", () => this.toggleTheme());
    });
  }

  /**
   * Update theme toggle button icons
   */
  updateThemeToggleButtons(theme) {
    const toggleButtons = document.querySelectorAll(".theme-toggle");
    const icon = theme === this.DARK_MODE ? "fa-sun" : "fa-moon";

    toggleButtons.forEach((button) => {
      button.innerHTML = `<i class="fas ${icon}"></i>`;
      button.title = theme === this.DARK_MODE ? "Mode clair" : "Mode sombre";
    });
  }

  /**
   * Get current theme
   */
  getCurrentTheme() {
    return localStorage.getItem(this.THEME_KEY) || this.LIGHT_MODE;
  }

  /**
   * Check if dark mode is active
   */
  isDarkMode() {
    return this.getCurrentTheme() === this.DARK_MODE;
  }
}

// Initialize theme manager only on catalogue and event-details pages
function initThemeManager() {
  var currentPath = window.location.pathname;
  var isThemePage =
    currentPath.includes("/catalogue") ||
    currentPath.includes("/event-details");

  if (isThemePage) {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", () => {
        window.themeManager = new ThemeManager();
      });
    } else {
      window.themeManager = new ThemeManager();
    }
  }
}

initThemeManager();
