import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { roomAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Building2, Users, MapPin, Search } from 'lucide-react';
import toast from 'react-hot-toast';

export default function RoomList() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await roomAPI.getAll();
      setRooms(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch rooms');
    } finally {
      setLoading(false);
    }
  };

  const filteredRooms = rooms.filter(room =>
    room.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Available Rooms</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">Browse all meeting rooms</p>
        </div>

        {/* Search Bar */}
        <div className="card mb-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
              placeholder="Search rooms by name..."
            />
          </div>
        </div>

        {/* Rooms Grid */}
        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
        ) : filteredRooms.length === 0 ? (
          <div className="card text-center py-12">
            <Building2 className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">No rooms found</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredRooms.map((room) => (
              <div key={room.id} className="card hover:shadow-xl transition-shadow">
                {/* Room Image */}
                <div className="h-48 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg mb-4 flex items-center justify-center">
                  <Building2 className="w-24 h-24 text-white opacity-50" />
                </div>

                {/* Room Details */}
                <h3 className="text-xl font-bold text-gray-900 mb-3">{room.name}</h3>

                <div className="space-y-2 mb-4">
                  <div className="flex items-center text-gray-600">
                    <Users className="w-5 h-5 mr-2" />
                    <span>Capacity: {room.capacity} people</span>
                  </div>

                  <div className="flex items-center text-gray-600">
                    <MapPin className="w-5 h-5 mr-2" />
                    <span>Floor {room.floor}</span>
                  </div>
                </div>

                {/* Amenities */}
                {room.amenities && (
                  <div className="mb-4">
                    <p className="text-sm font-medium text-gray-700 mb-2">Amenities:</p>
                    <div className="flex flex-wrap gap-2">
                      {room.amenities.split(',').map((amenity, index) => (
                        <span
                          key={index}
                          className="px-2 py-1 bg-blue-50 text-blue-600 text-xs rounded-full"
                        >
                          {amenity.trim()}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Book Button */}
                <Link
                  to="/employee/create-booking"
                  state={{ selectedRoomId: room.id }}
                  className="block w-full text-center bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors font-medium"
                >
                  Book This Room
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}