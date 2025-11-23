import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User, Calendar, Moon, Sun } from 'lucide-react';
import toast from 'react-hot-toast';
import { useState, useEffect } from 'react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isDark, setIsDark] = useState(false);

  useEffect(() => {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      setIsDark(true);
      document.documentElement.classList.add('dark');
    }
  }, []);

  const toggleTheme = () => {
    setIsDark((prev) => {
      const newTheme = !prev;
      if (newTheme) {
        document.documentElement.classList.add('dark');
        localStorage.setItem('theme', 'dark');
      } else {
        document.documentElement.classList.remove('dark');
        localStorage.setItem('theme', 'light');
      }
      return newTheme;
    });
  };

  const handleLogout = () => {
    logout();
    toast.success('Logged out successfully');
    navigate('/login');
  };

  // 👉 Determine Home URL based on role
  const homeLink =
    user?.role === 'ADMIN' ? '/admin/dashboard' : '/employee/dashboard';

  return (
    <nav className="bg-white dark:bg-gray-800 shadow-md transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">

          {/* Logo + Home Click */}
          <div className="flex items-center cursor-pointer">
            <Link to={homeLink} className="flex items-center">
              <Calendar className="w-8 h-8 text-blue-600 dark:text-blue-400 mr-2" />
              <span className="text-xl font-bold text-gray-800 dark:text-white">
                Room Booking
              </span>
            </Link>
          </div>

          {/* User Menu */}
          <div className="flex items-center space-x-4">
            {/* Dark Mode Toggle */}
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors"
              title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
            >
              {isDark ? (
                <Sun className="w-5 h-5 text-yellow-500" />
              ) : (
                <Moon className="w-5 h-5 text-gray-700" />
              )}
            </button>

            {/* User Info */}
            <div className="flex items-center space-x-2">
              <User className="w-5 h-5 text-gray-600 dark:text-gray-400" />
              <div className="text-sm">
                <p className="font-medium text-gray-800 dark:text-white">
                  {user?.fullName}
                </p>
                <p className="text-xs text-gray-500 dark:text-gray-400">
                  {user?.role}
                </p>
              </div>
            </div>

            {/* Logout */}
            <button
              onClick={handleLogout}
              className="flex items-center space-x-2 px-4 py-2 bg-red-50 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/50 transition-colors"
            >
              <LogOut className="w-4 h-4" />
              <span className="font-medium">Logout</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
}
