import { useState } from 'react';
import { reportAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Download, Calendar, FileText, TrendingUp } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Reports() {
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    startDate: '',
    endDate: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const downloadPDF = async (reportData, filename) => {
    try {
      const response = await reportData;
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      toast.success('Report downloaded successfully!');
    } catch (error) {
      toast.error('Failed to generate report');
    }
  };

  const handleCustomReport = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const reportData = reportAPI.generatePDF({
        startDate: formData.startDate,
        endDate: formData.endDate,
        userId: null,
        roomId: null,
        status: null,
      });

      await downloadPDF(reportData, `bookings_${formData.startDate}_to_${formData.endDate}.pdf`);
    } catch (error) {
      toast.error('Failed to generate custom report');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickReport = async (type) => {
    setLoading(true);
    try {
      let reportData;
      let filename;

      switch (type) {
        case 'current-month':
          reportData = reportAPI.getCurrentMonth();
          filename = 'current_month_report.pdf';
          break;
        case 'last-week':
          reportData = reportAPI.getLastWeek();
          filename = 'last_week_report.pdf';
          break;
        default:
          throw new Error('Invalid report type');
      }

      await downloadPDF(reportData, filename);
    } catch (error) {
      toast.error('Failed to generate report');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Reports & Analytics</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">Generate and download booking reports</p>
        </div>

        {/* Quick Reports */}
        <div className="card mb-8">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Quick Reports</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <button
              onClick={() => handleQuickReport('current-month')}
              disabled={loading}
              className="p-6 border-2 border-gray-200 rounded-xl hover:border-blue-500 hover:shadow-lg transition-all text-left disabled:opacity-50"
            >
              <div className="flex items-center space-x-4">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <Calendar className="w-8 h-8 text-blue-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 text-lg">Current Month</h3>
                  <p className="text-sm text-gray-600">All bookings this month</p>
                </div>
              </div>
            </button>

            <button
              onClick={() => handleQuickReport('last-week')}
              disabled={loading}
              className="p-6 border-2 border-gray-200 rounded-xl hover:border-blue-500 hover:shadow-lg transition-all text-left disabled:opacity-50"
            >
              <div className="flex items-center space-x-4">
                <div className="p-3 bg-green-100 rounded-lg">
                  <TrendingUp className="w-8 h-8 text-green-600" />
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 text-lg">Last 7 Days</h3>
                  <p className="text-sm text-gray-600">Recent booking activity</p>
                </div>
              </div>
            </button>
          </div>
        </div>

        {/* Custom Report Generator */}
        <div className="card">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Custom Date Range Report</h2>
          
          <form onSubmit={handleCustomReport} className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Start Date *
                </label>
                <input
                  type="date"
                  name="startDate"
                  value={formData.startDate}
                  onChange={handleChange}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  End Date *
                </label>
                <input
                  type="date"
                  name="endDate"
                  value={formData.endDate}
                  onChange={handleChange}
                  className="input-field"
                  min={formData.startDate}
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full flex items-center justify-center space-x-2 btn-primary disabled:opacity-50"
            >
              <Download className="w-5 h-5" />
              <span>{loading ? 'Generating...' : 'Generate & Download PDF'}</span>
            </button>
          </form>

          <div className="mt-6 p-4 bg-blue-50 rounded-lg">
            <div className="flex items-start space-x-3">
              <FileText className="w-5 h-5 text-blue-600 mt-0.5" />
              <div>
                <p className="text-sm text-blue-900 font-medium">Report Contents</p>
                <ul className="text-sm text-blue-800 mt-1 space-y-1">
                  <li>• All bookings within selected date range</li>
                  <li>• Booking details: date, time, room, status</li>
                  <li>• Summary statistics</li>
                  <li>• Professional PDF format ready to print</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        {/* Report Tips */}
        <div className="mt-6 card bg-gradient-to-r from-purple-50 to-pink-50">
          <h3 className="font-semibold text-gray-900 mb-2">💡 Tips</h3>
          <ul className="text-sm text-gray-700 space-y-1">
            <li>• Reports are generated in real-time with the latest data</li>
            <li>• PDF files can be printed or shared via email</li>
            <li>• Use custom date ranges for specific analysis periods</li>
            <li>• All timestamps are in your local timezone</li>
          </ul>
        </div>
      </div>
    </div>
  );
}